package com.github.zagum.speechrecognitionview.adapters

import android.os.Bundle
import android.speech.RecognitionListener

abstract class RecognitionListenerAdapter : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray) {}

    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {}

    override fun onResults(results: Bundle) {}

    override fun onPartialResults(partialResults: Bundle) {}

    override fun onEvent(eventType: Int, params: Bundle) {}
}
