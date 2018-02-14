package com.stasbar.concurrency.contentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

import java.util.HashMap

/**
 * Created by stasbar on 13.02.2018
 */

class MyCP : ContentProvider() {


    override fun onCreate(): Boolean {
        return false
    }

    @Synchronized
    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        val requestIdString = uri.lastPathSegment

        val cursor = MatrixCursor(columns)
        if (requestIdString == null) {
            db.values.forEach {
                cursor.addRow(arrayOf(it.id, it.name))
            }
        } else {
            val requestId = requestIdString.toInt()
            db[requestId]?.let { cursor.addRow(arrayOf(it.id, it.name)) }

        }

        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    @Synchronized
    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        if (contentValues.containsKey(DATA)) {
            val tmp = DataRecord(name = contentValues.getAsString(DATA))
            db[tmp.id] = tmp

            return Uri.withAppendedPath(CONTENT_URI, tmp.id.toString())
        }
        return null
    }

    @Synchronized
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val requestIdString = uri.lastPathSegment
        var removedCounter = 0
        if (requestIdString == null) {
            removedCounter = db.values.size
            db.values.clear()
        } else {
            val requestId = requestIdString.toInt()
            if (db.containsKey(requestId)) {
                db.remove(requestId)
                removedCounter = 1
            }

        }

        return removedCounter
    }

    @Synchronized
    override fun update(uri: Uri, contentValues: ContentValues, s: String?, strings: Array<String>?): Int {
        val requestIdString = uri.lastPathSegment
        var updatedCounter = 0
        if (requestIdString != null) {
            val requestId = requestIdString.toInt()
            if (db.containsKey(requestId)) {
                db[requestId] = DataRecord(id = requestId, name = contentValues.getAsString(DATA))
                updatedCounter = 1
            }

        }
        return updatedCounter
    }

    companion object {
        val CONTENT_URI = Uri.parse("content://com.stasbar.concurrency")
        val _ID = "_id"
        val DATA = "data"
        val columns = arrayOf(_ID, DATA)
        val db: HashMap<Int, DataRecord> = HashMap()

        val contentTypeSingle = "vnd.android.cursor.item/myContentProvider.data.text"
        val contentTypeDir = "vnd.android.cursor.dir/myContentProvider.data.text"

    }
}
