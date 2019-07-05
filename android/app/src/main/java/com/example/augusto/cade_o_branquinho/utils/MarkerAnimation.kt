package com.example.augusto.cade_o_branquinho.utils

import android.os.Handler
import android.support.v4.os.HandlerCompat.postDelayed
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import com.google.android.gms.maps.model.Marker


class BounceAnimation (private val mMarker: Marker, private val mHandler: Handler) : Runnable {

    private val mDuration = 1500L
    private val mInterpolator: Interpolator
    private val mStart: Long = SystemClock.uptimeMillis()

    init {
        mInterpolator = BounceInterpolator()
    }

    override fun run() {
        val elapsed = SystemClock.uptimeMillis() - mStart
        val t = Math.max(1 - mInterpolator.getInterpolation(elapsed.toFloat() / mDuration), 0f)
        mMarker.setAnchor(0.5f, 1.0f + 1.2f * t)

        if (t > 0.0) {
            // Post again 16ms later.
            mHandler.postDelayed(this, 16L)
        }
    }
}
