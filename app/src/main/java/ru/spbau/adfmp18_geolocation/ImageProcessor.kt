package ru.spbau.adfmp18_geolocation

import android.content.Context
import android.graphics.Bitmap

import ru.spbau.adfmp18_geolocation.Database.photosDBHelper


import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_features2d
import org.bytedeco.javacpp.opencv_xfeatures2d
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter


class ImageInfo(val resId: Int, val name: String, var lat: Double, var lon: Double)

class ImageProcessor (context: Context){
    private val dbHelper = photosDBHelper(context)
    private val parent = context

    fun getRandomPicture(): ImageInfo {
        var entry = dbHelper.readRandomPhoto()

        val resources = parent.resources
        val resourceId = resources.getIdentifier(entry.photoRes, "drawable",
                parent.packageName)

        return ImageInfo(resourceId, entry.photoName, entry.photoLat.toDouble(), entry.photoLon.toDouble())
    }

    fun compareImages(left: Bitmap, right: Bitmap): Boolean {
        var images = arrayOf(bitmapToMat(left), bitmapToMat(right))

        val hessianThreshold = 2500.0
        val nOctaves = 4
        val nOctaveLayers = 2
        val extended = true
        val upright = false
        val surf = opencv_xfeatures2d.SURF.create(hessianThreshold, nOctaves, nOctaveLayers, extended, upright)
        //  val surfDesc = DescriptorExtractor.create("SURF")
        //  val surfDesc = SurfDescriptorExtractor.create("SURF")
        var keyPoints = arrayOf(KeyPointVector(), KeyPointVector())
        var descriptors = arrayOf(Mat(), Mat())

        for (i in 0..1) {
            surf.detect(images[i], keyPoints[i])
            surf.compute(images[i], keyPoints[i], descriptors[i])
        }

        val matcher = opencv_features2d.BFMatcher(NORM_L2, false)

        var matches = DMatchVector()
        matcher.match(descriptors[0], descriptors[1], matches)
        val totalSize = matches.size()

        var minDist = (100.0).toFloat()
        for(i in 0..totalSize - 1)
        {
            var currMatch = matches[i]
            minDist = minOf(currMatch.distance(), minDist)
        }

        var goodMatches = 0
        var EPS = 0.05
        for(i in 0..totalSize - 1)
        {
            var currMatch = matches[i]
            if(currMatch.distance() < minDist*1.5 + EPS) {
                goodMatches += 1
            }
        }

        println("$goodMatches of $totalSize")
        return (1.0*goodMatches / matches.size()) > 0.25
    }

    private fun bitmapToMat(src: Bitmap): Mat {
        val tmp = src.copy(Bitmap.Config.ARGB_8888, true)
        val bitmapToFrame = AndroidFrameConverter()
        val frameToMat = OpenCVFrameConverter.ToMat()

        val fr = bitmapToFrame.convert(tmp)
        val res = frameToMat.convert(fr)

        return res
    }
}