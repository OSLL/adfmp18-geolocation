package ru.spbau.adfmp18_geolocation

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {

    private var touchLocationX = 0
    private var touchLocationY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        gridview.adapter = ImageAdapter(this)

        gridview.setOnItemClickListener { parent, v, position, id ->
            var a: Toast = Toast.makeText(this@GalleryActivity, R.string.some_picture_text, Toast.LENGTH_LONG)
            a.setGravity(Gravity.NO_GRAVITY, touchLocationX, touchLocationY)
            a.show()
        }
//                AdapterView.OnItemClickListener { parent, v, position, id ->
//                    val toast = Toast.makeText(this, R.string.some_picture_text, Toast.LENGTH_LONG)
//                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, position)
//                    toast.show()
//                }

        gridview.setOnTouchListener { v, event ->
            touchLocationX = event.x.toInt()
            touchLocationY = event.y.toInt()
            return@setOnTouchListener super.onTouchEvent(event)
        }
    }}
