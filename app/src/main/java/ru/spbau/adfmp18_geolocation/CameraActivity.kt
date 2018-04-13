package ru.spbau.adfmp18_geolocation

import android.app.PendingIntent.getActivity
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.getNumberOfCameras
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_camera.*
import android.content.Intent




class CameraActivity : AppCompatActivity() {

    private val TAG = "CameraActivity"

    private val mPicture = object : PictureCallback {

        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            // TODO: use a server handler for comparison
            // e.g.
            // bool handler(byte[] data)
            //
            // if matched, proceed to win screen
            // otherwise, back to camera
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val mCamera = getCameraInstance()
        capture_button.setOnClickListener({ _ ->
            mCamera?.takePicture(null, null, mPicture)})
    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
    }

}
