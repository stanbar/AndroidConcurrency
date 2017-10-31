package com.stasbar.concurrency.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CursorAdapter.FLAG_AUTO_REQUERY
import android.support.v4.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
import android.widget.SimpleCursorAdapter
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_db.*

class DBActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db)
        val dbHelper = ArtistDatabaseHelper(applicationContext)
        db = dbHelper.writableDatabase
        insertArtists()
        deletePih()
        val c = readArtists()
        displayArtists(c)

    }

    private fun insertArtists() {
        val values = ContentValues()
        values.put("name", "Pih") // Where name is the column
        db.insert(ARTISTS_TABLE, null, values)
        values.clear()

        values.put("name", "Peja")
        db.insert(ARTISTS_TABLE, null, values)
        values.clear()

        values.put("name", "ZDR")
        db.insert(ARTISTS_TABLE, null, values)
        values.clear()

    }

    private fun deletePih() {
        db.delete(ARTISTS_TABLE, "name=?", arrayOf("Pih"))

    }

    private fun readArtists(): Cursor {
        //return  db.rawQuery("SELECT _id, name FROM $ARTISTS_TABLE",null)

        return db.query(ARTISTS_TABLE, arrayOf("_id", "name"), null, null, null, null, null)
    }


    private fun displayArtists(c: Cursor) {
        val adapter = SimpleCursorAdapter(this, R.layout.list_layout, c, arrayOf("_id", "name"), intArrayOf(R.id._id, R.id.name), FLAG_REGISTER_CONTENT_OBSERVER)
        setListAdapter(adapter)

    }

    private fun setListAdapter(adapter: SimpleCursorAdapter) {
        listView.adapter = adapter

    }

    companion object {
        lateinit var db: SQLiteDatabase
        const val ARTISTS_TABLE = "artists"
    }
}
