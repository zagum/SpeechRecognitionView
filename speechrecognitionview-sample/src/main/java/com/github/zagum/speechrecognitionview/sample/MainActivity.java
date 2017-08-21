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

package com.github.zagum.speechrecognitionview.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;

  private SpeechRecognizer speechRecognizer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    int[] colors = {
        ContextCompat.getColor(this, R.color.color1),
        ContextCompat.getColor(this, R.color.color2),
        ContextCompat.getColor(this, R.color.color3),
        ContextCompat.getColor(this, R.color.color4),
        ContextCompat.getColor(this, R.color.color5)
    };

    int[] heights = { 20, 24, 18, 23, 16 };

    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

    final RecognitionProgressView recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
    recognitionProgressView.setSpeechRecognizer(speechRecognizer);
    recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
      @Override
      public void onResults(Bundle results) {
        showResults(results);
      }
    });
    recognitionProgressView.setColors(colors);
    recognitionProgressView.setBarMaxHeightsInDp(heights);
    recognitionProgressView.setCircleRadiusInDp(2);
    recognitionProgressView.setSpacingInDp(2);
    recognitionProgressView.setIdleStateAmplitudeInDp(2);
    recognitionProgressView.setRotationRadiusInDp(10);
    recognitionProgressView.play();

    Button listen = (Button) findViewById(R.id.listen);
    Button reset = (Button) findViewById(R.id.reset);

    listen.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
          requestPermission();
        } else {
          startRecognition();
          recognitionProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
              startRecognition();
            }
          }, 50);
        }
      }
    });

    reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        recognitionProgressView.stop();
        recognitionProgressView.play();
      }
    });
  }

  @Override
  protected void onDestroy() {
    if (speechRecognizer != null) {
      speechRecognizer.destroy();
    }
    super.onDestroy();
  }

  private void startRecognition() {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
    speechRecognizer.startListening(intent);
  }

  private void showResults(Bundle results) {
    ArrayList<String> matches = results
        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    Toast.makeText(this, matches.get(0), Toast.LENGTH_LONG).show();
  }

  private void requestPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.RECORD_AUDIO)) {
      Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show();
    } else {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.RECORD_AUDIO },
          REQUEST_RECORD_AUDIO_PERMISSION_CODE);
    }
  }
}