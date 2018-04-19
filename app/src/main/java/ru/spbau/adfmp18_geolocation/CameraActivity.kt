package ru.spbau.adfmp18_geolocation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.getNumberOfCameras
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : AppCompatActivity() {

    private val TAG = "CameraActivity"
    private var manager : LocationManager? = null
    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if(!checkDistance(location)) {
                println("too far away")
//                displayAlert()
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    private val imageProcessor = ImageProcessor(this)
    private var imageInfo: ImageInfo? = null

    private val mPicture = object : PictureCallback {

        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            val view = findViewById<ImageView>(R.id.camera_image)
            val target = (view.drawable as BitmapDrawable).bitmap

            val img  = BitmapFactory.decodeByteArray(data, 0, data.size)

            if(imageProcessor.compareImages(target, img)) {
                val intent = Intent(this@CameraActivity, ResultActivity::class.java)
                startActivity(intent)
            } else {
                val a: Toast = Toast.makeText(this@CameraActivity, R.string.photo_not_matched, Toast.LENGTH_SHORT)
                a.setGravity(Gravity.CENTER, 0, 0)
                a.show()
            }
        }
    }

    fun close() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Log.e(TAG, "Falling back: unable to obtain camera")
        startActivity(intent)
    }

    /** A safe way to get an instance of the Camera object.  */
    private fun getCameraInstance(): Camera? {
        var c: Camera? = null
        for (i in 0 until getNumberOfCameras()) {
            try {
                c = Camera.open(i) // attempt to get a Camera instance
                Log.e(TAG, "Camera $i here, Cap'n!")
                return c
            } catch (e: Exception) {
                // Camera is not available (in use or does not exist)

                // Show the dialog
                // 1. Instantiate an AlertDialog.Builder with its constructor
                val builder = AlertDialog.Builder(this)
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Camera $i is not available")
                        .setPositiveButton("Ok", null)
                // 3. Get the AlertDialog from create()
                val dialog = builder.create()
                Log.e(TAG, "Camera $i is not available", e)
            }
        }
        close()
        return null // just so that kotlin does not complain
    }

    fun showResults(v: View) {
        Log.e(TAG, "SHOW RESULT")

        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

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
        manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, (10.0).toFloat(), listener)

// TODO:        val mCamera = getCameraInstance()
        capture_button.setOnClickListener(::showResults)
// TODO: { _ -> mCamera?.takePicture(null, null, mPicture)})

    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
    }

    private fun displayAlert()
    {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var popupView = inflater.inflate(R.layout.activity_camera, null)
        val pop = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT)

        pop.showAsDropDown(popupView, 50, -30)
    }

    private fun checkDistance(from: Location): Boolean {
        val to = Location(LocationManager.GPS_PROVIDER)
        to.latitude = imageInfo?.lat!!
        to.longitude = imageInfo?.lon!!

        return from.distanceTo(to) < 1000
    }

    private fun successComparison() {
        println("Wow, it is alive!")
    }

}
