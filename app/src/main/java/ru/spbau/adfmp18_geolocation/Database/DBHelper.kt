package ru.spbau.adfmp18_geolocation.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

class photosDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val parent = context
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
        assert(parent != null)

        SQL_CREATE_ENTRIES.forEach{s -> db.execSQL(s)}
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertPhoto(photo: PhotoModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.PhotoEntry.COLUMN_PHOTO_ID, photo.photoid)
        values.put(DBContract.PhotoEntry.COLUMN_NAME, photo.photoName)
        values.put(DBContract.PhotoEntry.COLUMN_PHOTO_RES, photo.photoRes)
        values.put(DBContract.PhotoEntry.COLUMN_LAT, photo.photoLat)
        values.put(DBContract.PhotoEntry.COLUMN_LON, photo.photoLon)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.PhotoEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deletePhoto(photoid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.PhotoEntry.COLUMN_PHOTO_ID+ " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(photoid)
        // Issue SQL statement.
        db.delete(DBContract.PhotoEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readPhoto(photoid: String): ArrayList<PhotoModel> {
        val photos = ArrayList<PhotoModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.PhotoEntry.TABLE_NAME + " WHERE " + DBContract.PhotoEntry.COLUMN_PHOTO_ID + "='" + photoid + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            SQL_CREATE_ENTRIES.forEach{s -> db.execSQL(s)}
            return ArrayList()
        }

        var name: String
        var res: String
        var lon: String
        var lat: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                name = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_NAME))
                res = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_PHOTO_RES))
                lat = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LAT))
                lon = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LON))
                
                photos.add(PhotoModel(photoid, res, name, lat, lon))
                cursor.moveToNext()
            }
        }
        return photos
    }

    fun readRandomPhoto(): PhotoModel {
        assert(writableDatabase != null)
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.PhotoEntry.TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null)
        } catch (e: SQLiteException) {
            SQL_CREATE_ENTRIES.forEach{s -> db.execSQL(s)}
            cursor = db.rawQuery("select * from " + DBContract.PhotoEntry.TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null)
        }

        var photo = PhotoModel("", "", "", "", "")
        if (cursor!!.moveToFirst()) {
            var photoid = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_PHOTO_ID))
            var name = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_NAME))
            var res = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_PHOTO_RES))
            var lat = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LAT))
            var lon = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LON))

            photo = PhotoModel(photoid, res, name, lat, lon)
        }

        return photo;
    }

    fun readAllphotos(): ArrayList<PhotoModel> {
        val photos = ArrayList<PhotoModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.PhotoEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            SQL_CREATE_ENTRIES.forEach{s -> db.execSQL(s)}
            return ArrayList()
        }

        var photoid: String
        var name: String
        var res: String
        var lat: String
        var lon: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                photoid = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_PHOTO_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_NAME))
                res = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_PHOTO_RES))
                lat = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LAT))
                lon = cursor.getString(cursor.getColumnIndex(DBContract.PhotoEntry.COLUMN_LON))

                photos.add(PhotoModel(photoid, res, name, lat, lon))
                cursor.moveToNext()
            }
        }
        return photos
    }

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "PhotoMeta.db"

        private val SQL_CREATE_TABLE =
                "CREATE TABLE " + DBContract.PhotoEntry.TABLE_NAME + " (" +
                        DBContract.PhotoEntry.COLUMN_PHOTO_ID + " TEXT PRIMARY KEY," +
                        DBContract.PhotoEntry.COLUMN_PHOTO_RES + " TEXT," +
                        DBContract.PhotoEntry.COLUMN_NAME + " TEXT," +
                        DBContract.PhotoEntry.COLUMN_LAT + " TEXT," +
                        DBContract.PhotoEntry.COLUMN_LON + " TEXT)"

        private val SQL_CREATE_ENTRIES: List<String> = listOf(
                "INSERT INTO photoMeta(photoId,photoRes,name,lon,lat) VALUES (\"1\",\"isaak_1\", \"Исаакиевский собор\",\"59.933032\",\"30.307523\");",
                "INSERT INTO photoMeta(photoId,photoRes,name,lon,lat) VALUES (\"2\",\"winter_palace_1\",\"Зимний дворец\",\"59.940849\",\"30.313226\");",
                "INSERT INTO photoMeta(photoId,photoRes,name,lon,lat) VALUES (\"3\",\"kazan_1\",\"Казанский собор\",\"59.934560\",\"30.324838\");")




        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.PhotoEntry.TABLE_NAME
    }

}
