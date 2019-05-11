/*
 * Copyright (C) 2016 Evgenii Zagumennyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.zagum.speechrecognitionview.animators

import android.graphics.Point
import com.github.zagum.speechrecognitionview.RecognitionBar
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import java.util.*

class TransformAnimator(private val bars: List<RecognitionBar>, private val centerX: Int, private val centerY: Int, private val radius: Int) : BarParamsAnimator {

    private var startTimestamp: Long = 0
    private var isPlaying: Boolean = false


    private var listener: OnInterpolationFinishedListener? = null
    private val finalPositions = ArrayList<Point>()

    override fun start() {
        isPlaying = true
        startTimestamp = System.currentTimeMillis()
        initFinalPositions()
    }

    override fun stop() {
        isPlaying = false
        if (listener != null) {
            listener!!.onFinished()
        }
    }

    override fun animate() {
        if (!isPlaying) return

        val currTimestamp = System.currentTimeMillis()
        var delta = currTimestamp - startTimestamp
        if (delta > DURATION) {
            delta = DURATION
        }

        for (i in bars.indices) {
            val bar = bars[i]

            val x = bar.startX + ((finalPositions[i].x - bar.startX) * (delta.toFloat() / DURATION)).toInt()
            val y = bar.startY + ((finalPositions[i].y - bar.startY) * (delta.toFloat() / DURATION)).toInt()

            bar.x = x
            bar.y = y
            bar.update()
        }


        if (delta == DURATION) {
            stop()
        }
    }

    private fun initFinalPositions() {
        val startPoint = Point()
        startPoint.x = centerX
        startPoint.y = centerY - radius
        for (i in 0 until RecognitionProgressView.BARS_COUNT) {
            val point = Point(startPoint)
            rotate(360.0 / RecognitionProgressView.BARS_COUNT * i, point)
            finalPositions.add(point)
        }
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private fun rotate(degrees: Double, point: Point) {

        val angle = Math.toRadians(degrees)

        val x = centerX + ((point.x - centerX) * Math.cos(angle) - (point.y - centerY) * Math.sin(angle)).toInt()

        val y = centerY + ((point.x - centerX) * Math.sin(angle) + (point.y - centerY) * Math.cos(angle)).toInt()

        point.x = x
        point.y = y
    }

    fun setOnInterpolationFinishedListener(listener: OnInterpolationFinishedListener) {
        this.listener = listener
    }

    interface OnInterpolationFinishedListener {
        fun onFinished()
    }

    companion object {

        private const val DURATION: Long = 300
    }
}