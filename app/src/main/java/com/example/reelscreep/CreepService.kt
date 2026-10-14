package com.example.reelscreep

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.media.Image
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.LinearInterpolator
import android.widget.ImageView

@SuppressLint("AccessibilityPolicy")
class CreepService : AccessibilityService() {

    private var overlayImage: ImageView? = null
    private var isDoomScrolling = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        android.util.Log.d("CREEP", "we are so alive baby")

        overlayImage = ImageView(this).apply {
            setImageResource(R.drawable.image)
            scaleType = ImageView.ScaleType.FIT_XY
            alpha = 0f
            visibility = android.view.View.INVISIBLE
        }

        val paramas = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.addView(overlayImage, paramas)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        if (pkg == "com.example.reelscreep" || pkg == "com.android.systemui") return
        if (pkg != "com.instagram.android" && pkg != "com.google.android.youtube") {
            setDoomScrollState(false)
            return
        }
        val pm = getSystemService(POWER_SERVICE) as android.os.PowerManager
        if (!pm.isInteractive) {
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

    private val fullCreepDuration = 15000L

    private fun setDoomScrollState(active: Boolean) {
        if (isDoomScrolling == active) return
        isDoomScrolling = active

        val img = overlayImage ?: return

        val currentAlpha = img.alpha

        if (active) {
            android.util.Log.d("CREEP", "DOOOOOOMSCROLLING")

            val timeToFull = ((1f - currentAlpha) * fullCreepDuration).toLong()

            img.visibility = android.view.View.VISIBLE


            img.animate()
                .alpha(1f)
                .setDuration(timeToFull)
                .setInterpolator(LinearInterpolator())
                .start()

        } else {
            android.util.Log.d("CREEP", "stopped")

            val timeToZero = (currentAlpha * fullCreepDuration).toLong()

            img.visibility = android.view.View.INVISIBLE

            img.animate()
                .alpha(0f)
                .setDuration(timeToZero)
                .setInterpolator(LinearInterpolator())
                .start()
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