package com.example.nodoff.camera

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EyeStatus {
    IDLE,
    EYES_OPEN,
    EYES_CLOSED
}

data class EyeTrackingState(
    val status: EyeStatus,
    val closedDurationMs: Long
)

class EyeTrackerManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private val _state = MutableStateFlow(EyeTrackingState(EyeStatus.IDLE, 0L))
    val state: StateFlow<EyeTrackingState> = _state.asStateFlow()

    private var closedStartTime: Long? = null

    // Configure Face Detector with Classification Mode enabled
    private val detectorOptions = FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(detectorOptions)

    fun start() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Headless Image Analysis Use Case (no Preview)
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val executor = ContextCompat.getMainExecutor(context)
            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                // Fail-safe handling for device binding issues
                _state.value = EyeTrackingState(EyeStatus.IDLE, 0L)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stop() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            closedStartTime = null
            _state.value = EyeTrackingState(EyeStatus.IDLE, 0L)
        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        closedStartTime = null
                        _state.value = EyeTrackingState(EyeStatus.IDLE, 0L)
                        return@addOnSuccessListener
                    }

                    val face = faces.first()
                    val leftOpen = face.leftEyeOpenProbability
                    val rightOpen = face.rightEyeOpenProbability

                    if (leftOpen == null || rightOpen == null) {
                        closedStartTime = null
                        _state.value = EyeTrackingState(EyeStatus.IDLE, 0L)
                        return@addOnSuccessListener
                    }

                    // Trigger state change if both eyes' open probability falls below 0.2
                    if (leftOpen < 0.2f && rightOpen < 0.2f) {
                        val startTime = closedStartTime ?: System.currentTimeMillis().also { closedStartTime = it }
                        val duration = System.currentTimeMillis() - startTime
                        _state.value = EyeTrackingState(EyeStatus.EYES_CLOSED, duration)
                    } else {
                        closedStartTime = null
                        _state.value = EyeTrackingState(EyeStatus.EYES_OPEN, 0L)
                    }
                }
                .addOnFailureListener {
                    closedStartTime = null
                    _state.value = EyeTrackingState(EyeStatus.IDLE, 0L)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
