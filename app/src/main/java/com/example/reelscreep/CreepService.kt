package com.example.reelscreep

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.view.accessibility.AccessibilityNodeInfo

@SuppressLint("AccessibilityPolicy")
class CreepService : AccessibilityService() {

    private var lastLogTime = 0L

    private var isDoomScrolling = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        android.util.Log.d("CREEP", "we are so alive baby")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (pkg == "com.example.reelscreep" || pkg == "com.android.systemui") return
        if (pkg != "com.instagram.android" && pkg != "com.google.android.youtube") {
            setDoomScrollState(false)
            return
        }

        val ids = mutableSetOf<String>()
        getVisibleScreenIds(rootInActiveWindow, ids)

        if (ids.isEmpty()) return

        val inInstagramReels = pkg == "com.instagram.android" &&
                "clips_viewer_view_pager" in ids &&
                "reel_viewer_message_composer" !in ids

        val inYoutubeShorts = pkg == "com.google.android.youtube" &&
                "reel_watch_fragment_root" in ids

        setDoomScrollState(inInstagramReels || inYoutubeShorts)

    }

    private fun setDoomScrollState(active: Boolean) {
        if (isDoomScrolling == active) return

        isDoomScrolling = active

        if (active) {
            android.util.Log.d("CREEP", "DOOOOOOMSCROLLING")
        } else {
            android.util.Log.d("CREEP", "stopped")
        }
    }

    private fun getVisibleScreenIds(node: AccessibilityNodeInfo?, ids: MutableSet<String>) {
        if (node == null || !node.isVisibleToUser) return

        node.viewIdResourceName?.let {
            val shortId = it.substringAfter(":id/")
            ids.add(if (node.isSelected) "$shortId[SELECTED]" else shortId)
        }

        for (i in 0 until node.childCount) {
            getVisibleScreenIds(node.getChild(i), ids)
        }
    }

    override fun onInterrupt() {}


}