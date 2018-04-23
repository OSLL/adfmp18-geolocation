package ru.spbau.adfmp18_geolocation

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule

import android.support.test.runner.AndroidJUnit4
import android.support.test.rule.ActivityTestRule;
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.anything

@RunWith(AndroidJUnit4::class)
class IntegrationTester {
    @get:Rule
    public var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testGoToGallery() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onView(withId(R.id.gridview)).check(matches(isDisplayed()))
    }

    @Test
    fun testTakePicture() {
        onView(withId(R.id.playBtn)).perform(click())
        onView(withId(R.id.capture_button)).perform(click())

        // т.к. изображение с камеры случайное, то точно не совпадает с оригиналом
        onView(withId(R.id.camera_image)).check(matches(isDisplayed()))
    }

    @Test
    fun testSimpleMenuWalk() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onData(anything()).atPosition(0).perform(click())
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.playBtn)).check(matches(isDisplayed()))
    }

    @Test
    fun testComplexMenuWalk() {
        onView(withId(R.id.galleryBtn)).perform(click())
        onView(isRoot()).perform(swipeDown())
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.playBtn)).check(matches(isDisplayed()))

        onView(withId(R.id.playBtn)).perform(click())
        onView(withId(R.id.capture_button)).perform(click())

        onView(withId(R.id.camera_image)).check(matches(isDisplayed()))
    }
}