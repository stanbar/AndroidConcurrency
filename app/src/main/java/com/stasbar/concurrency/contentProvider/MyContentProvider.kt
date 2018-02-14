package com.stasbar.concurrency.contentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.dropTable
import org.jetbrains.anko.db.insertOrThrow

/**
 * Created by stasbar on 14.02.2018
 */
class MyContentProvider : ContentProvider() {

    lateinit var db: MyDB
    override fun onCreate(): Boolean {
        db = MyDB(context)
        return false
    }

    @Synchronized
    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        val requestIdString = uri.lastPathSegment
        val columns = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
        val sort = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        when {
            requestIdString == null ->
                return db.readableDatabase.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        columns,
                        null,
                        null,
                        null,
                        null,
                        null
                )
            requestIdString.toIntOrNull() != null -> {
                return db.readableDatabase.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        columns,
                        "${BaseColumns._ID} = ?",
                        arrayOf(requestIdString),
                        null,
                        null,
                        null
                )
            }
            else -> { // is some kind of string
                val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
                val selectionArgs = arrayOf(requestIdString)

                return db.readableDatabase.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sort
                )
            }
        }
    }



    @Synchronized
    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        if (contentValues.containsKey(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)
                && contentValues.containsKey(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)) {

            val id = db.writableDatabase.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, contentValues)

            return Uri.withAppendedPath(CONTENT_URI, id.toString())
        }
        return null
    }

    @Synchronized
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val lastSegment = uri.lastPathSegment
        when {
            lastSegment == null ->
                return db.writableDatabase.delete(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        null,
                        null
                )
            lastSegment.toIntOrNull() != null -> {
                val selection = "${BaseColumns._ID} = ?"
                val selectionArgs = arrayOf(lastSegment)
                return db.writableDatabase.delete(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                )
            }
            else -> { // is some kind of string
                val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
                val selectionArgs = arrayOf(lastSegment)
                return db.writableDatabase.delete(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                )
            }
        }

    }

    @Synchronized
    override fun update(uri: Uri, contentValues: ContentValues, s: String?, strings: Array<String>?): Int {
        val requestIdString = uri.lastPathSegment
        if (requestIdString != null) {
            return db.writableDatabase.update(FeedReaderContract.FeedEntry.TABLE_NAME, contentValues, "${BaseColumns._ID} = ?", arrayOf(requestIdString))
        }
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        val CONTENT_URI = Uri.parse("content://com.stasbar.concurrency")
        val columns = arrayOf(BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)

        val contentTypeSingle = "vnd.android.cursor.item/myContentProvider.data.text"
        val contentTypeDir = "vnd.android.cursor.dir/myContentProvider.data.text"

    }
}