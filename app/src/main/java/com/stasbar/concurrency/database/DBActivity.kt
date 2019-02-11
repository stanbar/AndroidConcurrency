package com.stasbar.concurrency.database

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
import com.stasbar.concurrency.R
import com.stasbar.concurrency.database.ArtistDatabaseHelper.Companion.DB_NAME
import kotlinx.android.synthetic.main.activity_db.*

class DBActivity : AppCompatActivity() {
    companion object {
        lateinit var db: SQLiteDatabase
        const val ARTISTS_TABLE = "artists"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db)
        val cr = contentResolver
        val dbHelper = ArtistDatabaseHelper(applicationContext)
        db = dbHelper.writableDatabase
        insertArtists()
        deletePih()

        var c = readArtists()
        displayArtists(c)

        insertMurzyn(cr)
        c = readArtists()
        displayArtists(c)

        applyBatchOf3w(cr)

        readArtists(cr)
    }

    private fun applyBatchOf3w(cr: ContentResolver) {
        val uri = Uri.parse("content://$DB_NAME/$ARTISTS_TABLE")
        val authority = "me"
        val operationMurzyn = ContentProviderOperation.newInsert(uri).withValue("name", "MurzynZDR").build()
        val operationDobo = ContentProviderOperation.newInsert(uri).withValue("name", "MurzynZDR").build()
        val operationTPS = ContentProviderOperation.newInsert(uri).withValue("name", "MurzynZDR").build()
        val operationWieszak = ContentProviderOperation.newInsert(uri).withValue("name", "MurzynZDR").build()
        val operations = arrayListOf(operationDobo, operationMurzyn, operationTPS, operationWieszak)
        cr.applyBatch(authority, operations)
    }

    private fun insertMurzyn(cr: ContentResolver) {
        val uri = Uri.parse("content://$DB_NAME/$ARTISTS_TABLE")
        val values = ContentValues()
        values.put("name", "Murzyn")
        cr.insert(uri, values)
    }

    private fun readArtists(cr: ContentResolver): Cursor {
        val uri = Uri.parse("content://$DB_NAME/$ARTISTS_TABLE")
        val selection: String? = null
        val selectionArgs: Array<String?>? = null
        val sortOrder: String? = null
        return cr.query(uri, arrayOf("_id", "name"), selection, selectionArgs, sortOrder)
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


}
