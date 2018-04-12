package ru.spbau.adfmp18_geolocation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.spbau.adfmp18_geolocation.R.id.playBtn

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playBtn.setOnClickListener(::launchCamera)
    }

    fun launchCamera(v: View) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}
