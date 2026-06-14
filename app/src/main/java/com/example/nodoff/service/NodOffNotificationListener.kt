package com.example.nodoff.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NodOffNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Stub for future phases (e.g., detecting and pausing playing media)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Stub for future phases
    }
}
