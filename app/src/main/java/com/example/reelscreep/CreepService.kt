package com.example.reelscreep

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

@SuppressLint("AccessibilityPolicy")
class CreepService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        android.util.Log.d("CREEP", "test log xd")
        Toast.makeText(this, "we are so alive baby", Toast.LENGTH_LONG).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (pkg == "com.instagram.android" || pkg == "com.google.android.youtube") {
            android.util.Log.d("CREEP", "in, $pkg and ${event.eventType}")
        }
    }

    override fun onInterrupt() {
    }

}