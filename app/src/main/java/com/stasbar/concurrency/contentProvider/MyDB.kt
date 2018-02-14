package com.stasbar.concurrency.contentProvider

import android.content.Context
import android.content.res.AssetManager
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by stasbar on 14.02.2018
 */

object FeedReaderContract{
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }
}

class MyDB(context : Context) : SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {
    internal var context: Context? = null
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        //executeSQLScript(db,"create.sql")
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


    private fun executeSQLScript(database: SQLiteDatabase, name: String) {
        val outputStream = ByteArrayOutputStream()
        val assetManager = context!!.assets
        try {
            outputStream.use {
                assetManager.open(name).use {
                    it.copyTo(outputStream);
                }
            }

            val createScript = outputStream.toString().split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in createScript.indices) {
                val sqlStatement = createScript[i].trim { it <= ' ' }
                // TODO You may want to parse out comments here
                if (sqlStatement.isNotEmpty()) {
                    database.execSQL(sqlStatement + ";")
                }
            }
        } catch (e: IOException) {
            // TODO Handle Script Failed to Load
        } catch (e: SQLException) {
            // TODO Handle Script Failed to Execute
        }

    }

    companion object {
        const val DB_NAME = "test.db"
        const val DB_VERSION = 1
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} TEXT," +
                "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} TEXT )"


        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXIST ${FeedReaderContract.FeedEntry.TABLE_NAME}"
    }


}
