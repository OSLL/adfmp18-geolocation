package ru.spbau.adfmp18_geolocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.google.android.cameraview.CameraView
import com.pnikosis.materialishprogress.ProgressWheel
import kotlinx.android.synthetic.main.activity_camera.*
import kotlin.concurrent.thread


class CameraActivity : AppCompatActivity() {

    private val TAG = "CameraActivity"
    private var manager : LocationManager? = null
    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if(!checkDistance(location)) {
                displayAlert()
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    private val imageProcessor = ImageProcessor(this)
    private var imageInfo: ImageInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA), 1)
//        }

        imageInfo = imageProcessor.getRandomPicture()

        setContentView(R.layout.activity_camera)
        camera_image.setImageResource(imageInfo?.resId!!)

        // because it's kotlin
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, (10.0).toFloat(), listener)

        camera.addCallback(object : CameraView.Callback() {

            override fun onCameraOpened(cameraView: CameraView?) {
                Log.d(TAG, "OnCameraOpened")
            }

            override fun onCameraClosed(cameraView: CameraView?) {
                Log.d(TAG, "OnCameraClosed")
            }

            override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
                Log.d(TAG, "OnPictureTaken")
                if(data != null) {
                    checkPicture(data)
                } else {
                    Log.d(TAG, "PictureIsNull")
                }
            }
        })
        capture_button.setOnClickListener {camera.takePicture()}
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            camera.start();
        } else {
            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.CAMERA), 1);
        }
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
    }

    private fun displayAlert()
    {
        val text = "You are too far away from destination!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

    private fun checkDistance(from: Location): Boolean {
        val to = Location(LocationManager.GPS_PROVIDER)
        to.latitude = imageInfo?.lat!!
        to.longitude = imageInfo?.lon!!

        return from.distanceTo(to) < 1000
    }

    private fun checkPicture(data: ByteArray) {
        var wheel = ProgressWheel(this@CameraActivity)
        wheel.barColor = Color.BLUE
        wheel.spin()

        val view = findViewById<ImageView>(R.id.camera_image)
        val target = (view.drawable as BitmapDrawable).bitmap

        val img = BitmapFactory.decodeByteArray(data, 0, data.size)
        val areEqual = imageProcessor.compareImages(target, img)

        if(areEqual) {
            val intent = Intent(this@CameraActivity, ResultActivity::class.java)
            startActivity(intent)
        } else {
            val a: Toast = Toast.makeText(this@CameraActivity, R.string.photo_not_matched, Toast.LENGTH_SHORT)
            a.setGravity(Gravity.CENTER, 0, 0)
            a.show()
        }

        wheel.stopSpinning()
    }
}
