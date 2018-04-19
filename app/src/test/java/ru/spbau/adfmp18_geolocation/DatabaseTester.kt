package ru.spbau.adfmp18_geolocation

import android.graphics.BitmapFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import ru.spbau.adfmp18_geolocation.Database.photosDBHelper

import android.graphics.Bitmap
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
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
        var image_kaz_1 = getBitmapFromURL("http://globus.aquaviva.ru/upload/iblock/ee4/kazanskiy-sobor.jpg")
        var image_kaz_2 = getBitmapFromURL("http://kazansky-spb.ru/image/gallery/1.jpg")
        var image_zinger = getBitmapFromURL("http://www.visit-petersburg.ru/media/uploads/audioguide/38/38_cover.jpg.1050x500_q95_crop_upscale.jpg")

        println(image_kaz_1?.getPixel(100, 100))
        println(image_kaz_2?.getPixel(55, 55))
        println(image_zinger?.getPixel(55, 55))

        var isEq = proc.compareImages(image_kaz_1!!, image_kaz_2!!)
        var isEq2 = proc.compareImages(image_kaz_1!!, image_zinger!!)

        println(isEq)
        println(isEq2)
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            return null
        }

    }
}