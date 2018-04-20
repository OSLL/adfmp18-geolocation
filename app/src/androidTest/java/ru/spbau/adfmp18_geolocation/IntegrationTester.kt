package ru.spbau.adfmp18_geolocation

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule

import android.support.test.runner.AndroidJUnit4
import android.support.test.rule.ActivityTestRule;
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.InstrumentationRegistry
import android.graphics.BitmapFactory
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.toPackage

@RunWith(AndroidJUnit4::class)
class IntegrationTester {
    @Rule
    public var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testGoToGallery() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onView(withId(R.layout.activity_gallery)).check(matches(isDisplayed()))
    }

    @Test
    fun testTakePicture() {
        onView(withId(R.id.playBtn)).perform(click())
        onView(withId(R.id.capture_button)).perform(click())

        // т.к. изображение с камеры случайное, то точно не совпадает с оригиналом
        onView(withId(R.layout.activity_camera)).check(matches(isDisplayed()))
    }

    @Test
    fun testSimpleMenuWalk() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onView(withId(R.layout.activity_gallery)).perform(swipeDown())
        onView(withId(R.layout.activity_gallery)).perform(pressBack())

        onView(withId(R.layout.activity_main)).check(matches(isDisplayed()))
    }

    @Test
    fun testComplexMenuWalk() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onView(withId(R.layout.activity_gallery)).perform(swipeDown())
        onView(withId(R.layout.activity_gallery)).perform(pressBack())

        onView(withId(R.layout.activity_main)).check(matches(isDisplayed()))

        onView(withId(R.id.playBtn)).perform(click())
        onView(withId(R.id.capture_button)).perform(click())

        onView(withId(R.layout.activity_camera)).check(matches(isDisplayed()))
    }

    @Test
    fun testTakeCorrectPicture() {
        onView(withId(R.id.playBtn)).perform(click())
        onView(withId(R.id.capture_button)).perform(click())

        val target = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().resources,
                R.drawable.kazan_1)

        val resultData = Intent()
        resultData.putExtra("data", target)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result)

        onView(withId(R.layout.activity_result)).check(matches(isDisplayed()))
    }
}