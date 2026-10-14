package com.example.reelscreep

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.view.accessibility.AccessibilityNodeInfo

@SuppressLint("AccessibilityPolicy")
class CreepService : AccessibilityService() {

    private var lastLogTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (pkg != "com.instagram.android" || pkg != "com.google.android.youtube") return

        val now = System.currentTimeMillis()

        if (now - lastLogTime <1500) return
        lastLogTime = now

        val ids = mutableSetOf<String>()
        getScreenIds(rootInActiveWindow, ids)

        android.util.Log.d("CREEP", "[$pkg] $ids")

    }

    private fun getScreenIds(node: AccessibilityNodeInfo?, ids: MutableSet<String>) {
        if (node == null) return

        node.viewIdResourceName?.let {
            val shortId = it.substringAfter(":id/")
            ids.add(if (node.isSelected) "$shortId[SELECTED]" else shortId)
        }

        for (i in 0 until node.childCount) {
            getScreenIds(node.getChild(i), ids)
        }
    }

    override fun onInterrupt() {
    }

}