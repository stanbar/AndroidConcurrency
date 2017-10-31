package com.stasbar.concurrency.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by stasbar on 31.10.2017
 */
class ArtistDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "artist_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CMD)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    companion object {
        const val CREATE_CMD = "CREATE TABLE  artists (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)"
    }

}