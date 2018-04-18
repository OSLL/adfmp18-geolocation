package ru.spbau.adfmp18_geolocation

import android.graphics.BitmapFactory
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import ru.spbau.adfmp18_geolocation.Database.photosDBHelper
import java.util.*

import org.bytedeco.javacpp.opencv_imgcodecs.imread
import org.robolectric.res.Fs
import org.robolectric.manifest.AndroidManifest


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk= intArrayOf(26), packageName = "ru.spbau.adfmp18_geolocation",
        manifest = "")
class DatabaseTester {
    @Test
    fun testGetRandomEntry() {
        var usersDBHelper = photosDBHelper(RuntimeEnvironment.application)
        var entry = usersDBHelper.readPhoto("3")

        assert(entry[0].photoRes == "kazan_1")

        var randomEntry = usersDBHelper.readRandomPhoto();

        println(randomEntry.photoName)
    }

    @Test
    fun testGetRandomDrawable() {
        var proc = ImageProcessor(RuntimeEnvironment.application)
        var imageToDraw = proc.getRandomPicture()
    }

    @Test
    fun testCompareImages() {
        var proc = ImageProcessor(RuntimeEnvironment.application)

        var resTable = RuntimeEnvironment.getAppResourceTable()



        var kaz_drawable = RuntimeEnvironment.application.resources.getDrawable(R.drawable.kazan_1)
        var image_kaz_1 = BitmapFactory.decodeResource( RuntimeEnvironment.application.resources, R.drawable.kazan_1)
        var image_kaz_2 = BitmapFactory.decodeResource(RuntimeEnvironment.application.resources, R.drawable.kazan_1)
        var image_med_acad = BitmapFactory.decodeResource(RuntimeEnvironment.application.resources, R.drawable.med_acad_1)

        var isEq = proc.compareImages(image_kaz_1, image_kaz_2)
        var isEq2 = proc.compareImages(image_kaz_1, image_med_acad)

        println(isEq)
    }
}