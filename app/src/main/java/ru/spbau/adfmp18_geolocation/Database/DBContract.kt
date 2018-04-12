package ru.spbau.adfmp18_geolocation.Database

import android.provider.BaseColumns

object DBContract {
    /* Inner class that defines the table contents */
    class PhotoEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "photoMeta"
            val COLUMN_PHOTO_ID = "photoId"
            val COLUMN_PHOTO_RES = "photoRes"
            val COLUMN_NAME = "name"
            val COLUMN_LAT = "lat"
            val COLUMN_LON = "lon"
        }
    }
}