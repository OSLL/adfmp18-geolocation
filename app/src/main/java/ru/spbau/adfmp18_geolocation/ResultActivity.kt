package ru.spbau.adfmp18_geolocation

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    private val TAG = "ResultActivity"

    fun toMainMenu(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun continueGame(v: View) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "OnCreate")
        setContentView(R.layout.activity_result)

        res_to_main_button.setOnClickListener(::toMainMenu)
        res_to_play_button.setOnClickListener(::continueGame)
    }
}
