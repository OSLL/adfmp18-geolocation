package ru.spbau.adfmp18_geolocation

import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import ru.spbau.adfmp18_geolocation.Database.photosDBHelper

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk= intArrayOf(26), packageName = "ru.spbau.adfmp18_geolocation")
class DatabaseTester {

    @Test
    fun testsWork() {
        assertTrue(true)
    }

    @Test
    fun testGetRandomEntry() {
        var usersDBHelper = photosDBHelper(RuntimeEnvironment.application)
        var entry = usersDBHelper.readPhoto("3");

        assert(entry[0].photoRes == "kazan_1.png")

        var randomEntry = usersDBHelper.readRandomPhoto();

        println(randomEntry.photoName)
    }

    @Test
    fun testGetRandomDrawable() {
        var proc = ImageProcessor(RuntimeEnvironment.application)
        var imageToDraw = proc.getRandomPicture()
        println(imageToDraw)
    }
}