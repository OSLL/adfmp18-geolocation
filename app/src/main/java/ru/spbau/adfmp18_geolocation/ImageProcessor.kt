package ru.spbau.adfmp18_geolocation

import android.content.Context
import org.opencv.core.*

import ru.spbau.adfmp18_geolocation.Database.photosDBHelper

import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

//TODO: just do it!
class ImageProcessor (context: Context){
    private val dbHelper = photosDBHelper(context)
    private val parent = context

    fun getRandomPicture(): Int {
        var entry = dbHelper.readRandomPhoto()

        val resources = parent.getResources()
        val resourceId = resources.getIdentifier(entry.photoRes, "drawable",
                parent.getPackageName())


        return resourceId
    }

    fun compareImages(left: ByteArray, leftw: Int, lefth: Int, right: ByteArray, rightw: Int, righth: Int): Boolean {
        var matL = Mat(lefth, leftw, CvType.CV_8U)
        var matR = Mat(righth, rightw, CvType.CV_8U)

        matL.put(lefth, leftw, left)
        matR.put(righth, rightw, right)

        Imgproc.cvtColor(matL, matL, Imgproc.COLOR_RGB2GRAY)
        Imgproc.cvtColor(matR, matR, Imgproc.COLOR_RGB2GRAY)


        var detector = FeatureDetector.create(FeatureDetector.ORB)
        var descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB)
        var matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

        var descriptorsLeft = Mat()
        var keypointsLeft = MatOfKeyPoint()

        detector.detect(matL, keypointsLeft)
        descriptor.compute(matL, keypointsLeft, descriptorsLeft)

        var descriptorsRight = Mat()
        var keypointsRight = MatOfKeyPoint()

        detector.detect(matR, keypointsRight)
        descriptor.compute(matR, keypointsRight, descriptorsRight)

        var matches = MatOfDMatch()

        matcher.match(descriptorsLeft, descriptorsRight, matches)

        var matchesList = matches.toList()
        var minDist = matchesList.minBy { m -> m.distance }

        var minVal: Float

        if(minDist != null) {
            minVal = minDist.distance
        } else {
            minVal = (1.0).toFloat()
        }

        var posCount = matchesList.count { m -> m.distance < minVal*2.0 }

        return (1.0*posCount/matchesList.size) > 0.5
    }
}