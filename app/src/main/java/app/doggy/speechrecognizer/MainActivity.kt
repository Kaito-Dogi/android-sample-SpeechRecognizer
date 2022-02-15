package app.doggy.speechrecognizer

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.doggy.speechrecognizer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        // onRequestPermissionsResult()メソッドに渡されるリクエストコード．
        // 他のリクエストコードと重複しない値を使用する．
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }

    // SpeechRecognizerを代入する変数．
    private lateinit var speechRecognizer: SpeechRecognizer

    // 音声入力が開始されているか停止されているかを管理する変数．
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionsGranted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (permissionsGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        // SpeechRecognizerを生成．
        // シングルトン（staticオブジェクト）の寿命はApplicationの寿命と同じなので，applicationContextを渡す．
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        // RecognizerListenerを渡す．
        speechRecognizer.setRecognitionListener(createRecognitionListenerStringStream {
            binding.resultText.text = it
        })

        // クリックで音声入力の開始・停止を切り替える．
        binding.button.setOnClickListener {
            if (isRecording) {
                speechRecognizer.stopListening()
                binding.button.text = "START"
            } else {
                speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                binding.button.text = "STOP"
            }
            isRecording = !isRecording
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 使わなくなったSpeechRecognizerを破棄．
        speechRecognizer.destroy()
    }

    private fun createRecognitionListenerStringStream(onResult: (String) -> Unit): RecognitionListener {
        return object : RecognitionListener {

            override fun onRmsChanged(rmsdB: Float) {
                /* 今回は使用しない．*/
            }

            override fun onReadyForSpeech(params: Bundle) {
                onResult("onReadyForSpeech")
            }

            override fun onBufferReceived(buffer: ByteArray) {
                onResult("onBufferReceived")
            }

            override fun onPartialResults(partialResults: Bundle) {
                onResult("onPartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                onResult("onEvent")
            }

            override fun onBeginningOfSpeech() {
                onResult("onBeginningOfSpeech")
            }

            override fun onEndOfSpeech() {
                onResult("onEndOfSpeech")
            }

            override fun onError(error: Int) {
                when (error) {
                    ERROR_AUDIO -> onResult("AUDIO")
                    ERROR_CLIENT -> onResult("CLIENT")
                    ERROR_INSUFFICIENT_PERMISSIONS -> onResult("INSUFFICIENT_PERMISSIONS")
                    ERROR_LANGUAGE_NOT_SUPPORTED -> onResult("LANGUAGE_NOT_SUPPORTED")
                    ERROR_LANGUAGE_UNAVAILABLE -> onResult("LANGUAGE_UNAVAILABLE")
                    ERROR_NETWORK -> onResult("NETWORK")
                    ERROR_NETWORK_TIMEOUT -> onResult("NETWORK_TIMEOUT")
                    ERROR_NO_MATCH -> onResult("NO_MATCH")
                    ERROR_RECOGNIZER_BUSY -> onResult("RECOGNIZER_BUSY")
                    ERROR_SERVER -> onResult("SERVER")
                    ERROR_SERVER_DISCONNECTED -> onResult("SERVER_DISCONNECTED")
                    ERROR_SPEECH_TIMEOUT -> onResult("SPEECH_TIMEOUT")
                    ERROR_TOO_MANY_REQUESTS -> onResult("TOO_MANY_REQUESTS")
                    else -> onResult("UNKNOWN")
                }
                //onResult("onError")
            }

            override fun onResults(results: Bundle) {
                val stringArray =
                    results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                onResult("onResults " + stringArray.toString())
            }
        }
    }
}