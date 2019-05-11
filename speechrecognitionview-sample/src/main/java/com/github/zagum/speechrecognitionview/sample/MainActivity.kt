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

package com.github.zagum.speechrecognitionview.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter

class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colors = intArrayOf(ContextCompat.getColor(this, R.color.color1), ContextCompat.getColor(this, R.color.color2), ContextCompat.getColor(this, R.color.color3), ContextCompat.getColor(this, R.color.color4), ContextCompat.getColor(this, R.color.color5))

        val heights = intArrayOf(20, 24, 18, 23, 16)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val recognitionProgressView = findViewById<View>(R.id.recognition_view) as RecognitionProgressView
        recognitionProgressView.setSpeechRecognizer(speechRecognizer)
        recognitionProgressView.setRecognitionListener(object : RecognitionListenerAdapter() {
            override fun onResults(results: Bundle?) {
                showResults(results!!)
            }
        })
        recognitionProgressView.setColors(colors)
        recognitionProgressView.setBarMaxHeightsInDp(heights)
        recognitionProgressView.setCircleRadiusInDp(2)
        recognitionProgressView.setSpacingInDp(2)
        recognitionProgressView.setIdleStateAmplitudeInDp(2)
        recognitionProgressView.setRotationRadiusInDp(10)
        recognitionProgressView.play()

        val listen = findViewById<View>(R.id.listen) as Button
        val reset = findViewById<View>(R.id.reset) as Button

        listen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermission()
            } else {
                startRecognition()
                recognitionProgressView.postDelayed({ startRecognition() }, 50)
            }
        }

        reset.setOnClickListener {
            recognitionProgressView.stop()
            recognitionProgressView.play()
        }
    }

    override fun onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
        }
        super.onDestroy()
    }

    private fun startRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
        speechRecognizer!!.startListening(intent)
    }

    private fun showResults(results: Bundle) {
        val matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Toast.makeText(this, matches!![0], Toast.LENGTH_LONG).show()
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION_CODE)
        }
    }

    companion object {
        private val TAG = "MainActivity"
        private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1
    }
}