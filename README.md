# SpeechRecognitionView
Custom animation for Google Speech Recognizer.

#Description

This library provides google now - style animation for speech recognition.

![image](http://i.makeagif.com/media/4-30-2016/NB3I7e.gif)

#Compatibility

This library is compatible from API 15

#Gradle

Add the following to your project build.gradle
``` xml
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    compile 'com.github.zagum:SpeechRecognitionView:1.0'
}
```


#Usage

* Xml file:

Simply add view to your layout:

``` xml
<com.zagum.speechrecognitionview.RecognitionProgressView
	android:id="@+id/recognition_view"
	android:layout_width="110dp"
	android:layout_height="110dp"
	android:layout_gravity="center"/>
```
* Initialization:

Init speech recognizer:
``` java
SpeechRecognizer speechRecognizer = (RecognitionProgressView) findViewById(R.id.recognition_view);
```

Init RecognitionProgressView:
``` java
final RecognitionProgressView recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
recognitionProgressView.setSpeechRecognizer(speechRecognizer);
recognitionProgressView.setRecognitionListener(this);
```
Recognition listener is ```android.speech.RecognitionListener ```:
``` java
@Override
public void onReadyForSpeech(Bundle params) {
	Log.d(TAG, "onReadyForSpeech() called with: " + "params = [" + params + "]");
}

@Override
public void onBeginningOfSpeech() {
	Log.d(TAG, "onBeginningOfSpeech() called with: " + "");
}

@Override
public void onRmsChanged(float rmsdB) {
	Log.d(TAG, "onRmsChanged() called with: " + "rmsdB = [" + rmsdB + "]");
}

@Override
public void onBufferReceived(byte[] buffer) {
	Log.d(TAG, "onBufferReceived() called with: " + "buffer = [" + Arrays.toString(buffer) + "]");
}

@Override
public void onEndOfSpeech() {
	Log.d(TAG, "onEndOfSpeech() called with: " + "");
}

@Override
public void onError(int error) {
	Log.d(TAG, "onError() called with: " + "error = [" + error + "]");
}

@Override
public void onResults(Bundle results) {
	Log.d(TAG, "onResults() called with: " + "results = [" + results + "]");
}

@Override
public void onPartialResults(Bundle partialResults) {
	Log.d(TAG, "onPartialResults() called with: " + "partialResults = [" + partialResults + "]");
}

@Override
public void onEvent(int eventType, Bundle params) {
	Log.d(TAG, "onEvent() called with: " + "eventType = [" + eventType + "], params = [" + params + "]");
}
```

When SpeechRecognizer and RecognitionProgressView inited, use your speech recognizer as usual:
``` java
listen.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		startRecognition();
	}
});

private void startRecognition() {
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	speechRecognizer.startListening(intent);
}
```

Start and stop RecognitionProgressView animation:
``` java
recognitionProgressView.play();

recognitionProgressView.stop();
```

* Customization:

Set custom colors: 
``` java
int[] colors = {
		ContextCompat.getColor(this, R.color.color1),
		ContextCompat.getColor(this, R.color.color2),
		ContextCompat.getColor(this, R.color.color3),
		ContextCompat.getColor(this, R.color.color4),
		ContextCompat.getColor(this, R.color.color5)
};
recognitionProgressView.setColors(colors);
```

Set custom bars heights: 
``` java
int[] heights = {60, 76, 58, 80, 55};
recognitionProgressView.setBarMaxHeightsInDp(heights);
```
Don't forget to add permission to your AndroidManifest.xml file
``` xml
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
```


* Warning

From [Android Documentation](http://developer.android.com/reference/android/speech/RecognitionListener.html#onRmsChanged(float))
For ```java public abstract void onRmsChanged (float rmsdB)``` callback ```There is no guarantee that this method will be called.```, 
so if this callback does not return values the Bars animation will be skipped. 

I found some hack to make it working every time you want to start speech recognition:
``` java
listen.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		startRecognition();
		recognitionProgressView.postDelayed(new Runnable() {
			@Override
			public void run() {
				startRecognition();
			}
		}, 100);
	}
});
```


#Licence

    Copyright 2016 Evgenii Zagumennyi
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

