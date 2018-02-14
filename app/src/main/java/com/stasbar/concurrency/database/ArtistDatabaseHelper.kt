package com.stasbar.concurrency.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by stasbar on 31.10.2017
 */
class ArtistDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    companion object {
        const val CREATE_CMD = "CREATE TABLE  artists (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)"
        const val DB_NAME = "artist_db"
        const val _ID = "_id"
        const val DATA = "data"
        val columns = arrayOf(_ID, DATA)
        val contentTypeSingle = "vnd.android.cursor.item/myContentProvider.data.text"
        val contentTypeMultiple = "vnd.android .cursor.dir/myContentProvider.data.text"



    }

}