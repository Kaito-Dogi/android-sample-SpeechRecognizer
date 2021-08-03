package app.doggy.speechrecognizer

import android.Manifest.permission.INTERNET
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        // onRequestPermissionsResult()メソッドに渡されるリクエストコード．
        // 他のリクエストコードと重複しない値を使用する．
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionsGranted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (permissionsGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

    }
}