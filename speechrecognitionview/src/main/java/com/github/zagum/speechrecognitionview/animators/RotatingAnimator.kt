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
import android.view.animation.AccelerateDecelerateInterpolator
import com.github.zagum.speechrecognitionview.RecognitionBar
import java.util.*

class RotatingAnimator(private val bars: List<RecognitionBar>, private val centerX: Int, private val centerY: Int) : BarParamsAnimator {

    private var startTimestamp: Long = 0
    private var isPlaying: Boolean = false
    private val startPositions: MutableList<Point>

    init {
        this.startPositions = ArrayList()
        for (bar in bars) {
            startPositions.add(Point(bar.x, bar.y))
        }
    }

    override fun start() {
        isPlaying = true
        startTimestamp = System.currentTimeMillis()
    }

    override fun stop() {
        isPlaying = false
    }

    override fun animate() {
        if (!isPlaying) return

        val currTimestamp = System.currentTimeMillis()
        if (currTimestamp - startTimestamp > DURATION) {
            startTimestamp += DURATION
        }

        val delta = currTimestamp - startTimestamp

        val interpolatedTime = delta.toFloat() / DURATION

        val angle = interpolatedTime * ROTATION_DEGREES

        for ((i, bar) in bars.withIndex()) {
            var finalAngle = angle
            if (i > 0 && delta > ACCELERATE_ROTATION_DURATION) {
                finalAngle += decelerate(delta, bars.size - i)
            } else if (i > 0) {
                finalAngle += accelerate(delta, bars.size - i)
            }
            rotate(bar, finalAngle.toDouble(), startPositions[i])
        }
    }

    private fun decelerate(delta: Long, scale: Int): Float {
        val accelerationDelta = delta - ACCELERATE_ROTATION_DURATION
        val interpolator = AccelerateDecelerateInterpolator()
        val interpolatedTime = interpolator.getInterpolation(accelerationDelta.toFloat() / DECELERATE_ROTATION_DURATION)
        val decelerationAngle = -interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale)
        return ACCELERATION_ROTATION_DEGREES * scale + decelerationAngle
    }

    private fun accelerate(delta: Long, scale: Int): Float {
        val interpolator = AccelerateDecelerateInterpolator()
        val interpolatedTime = interpolator.getInterpolation(delta.toFloat() / ACCELERATE_ROTATION_DURATION)
        return interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale)
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private fun rotate(bar: RecognitionBar, degrees: Double, startPosition: Point) {

        val angle = Math.toRadians(degrees)

        val x = centerX + ((startPosition.x - centerX) * Math.cos(angle) - (startPosition.y - centerY) * Math.sin(angle)).toInt()

        val y = centerY + ((startPosition.x - centerX) * Math.sin(angle) + (startPosition.y - centerY) * Math.cos(angle)).toInt()

        bar.x = x
        bar.y = y
        bar.update()
    }

    companion object {

        private const val DURATION: Long = 2000
        private const val ACCELERATE_ROTATION_DURATION: Long = 1000
        private const val DECELERATE_ROTATION_DURATION: Long = 1000
        private const val ROTATION_DEGREES = 720f
        private const val ACCELERATION_ROTATION_DEGREES = 40f
    }
}