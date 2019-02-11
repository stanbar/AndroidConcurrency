package com.stasbar.concurrency.contentProvider.async

import android.app.ListActivity
import android.app.LoaderManager
import android.content.AsyncQueryHandler
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.SimpleCursorAdapter
import com.stasbar.concurrency.R
import com.stasbar.concurrency.contentProvider.Feed
import com.stasbar.concurrency.contentProvider.FeedReaderContract
import com.stasbar.concurrency.contentProvider.MyContentProvider


class ContactProviderAsyncActivity : ListActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The adapter that binds our data to the ListView
     */
    lateinit var mAdapter: SimpleCursorAdapter


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, MyContentProvider.CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        when (loader.id) {
            LOADER_ID -> {
                // Async load is complete & data is available for SimpleCursorAdapter
                mAdapter.swapCursor(data)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_provider)
        val rowLayout = android.R.layout.two_line_list_item
        val dataColumns = MyContentProvider.columns
        val viewIds = intArrayOf(android.R.id.text1, android.R.id.text2)
        mAdapter = SimpleCursorAdapter(this, rowLayout, null, dataColumns, viewIds, 0)
        listAdapter = mAdapter

        loaderManager.initLoader(LOADER_ID, null, this)
        val btc = Feed(title = "Bitcoin", subtitle = "Is crashing")
        val ltc = Feed(title = "Litecoin", subtitle = "Is great")
        val eth = Feed(title = "Ethereum", subtitle = "Is awesome")
        InsertQueryHandler(
            btc,
            InsertQueryHandler(
                ltc,
                InsertQueryHandler(
                    eth,
                    QueryQueryHandler()
                )
            )
        ).execute()


        listView.setOnItemClickListener { adapter, view, position, id ->
            delete(position)
        }
        listView.setOnItemLongClickListener { adapter, view, position, id ->
            update(position)
            true
        }
    }


    fun query(view: View? = null) {
        QueryQueryHandler().execute()
    }

    fun insert(view: View? = null) {
        val dataRecord = Feed()
        InsertQueryHandler(dataRecord, QueryQueryHandler()).execute()
    }

    private fun delete(position: Int) {
        val cursor = mAdapter.cursor

        if (cursor.moveToPosition(position)) {
            val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            DeleteQueryHandler(id).execute()
        }
    }


    private fun update(position: Int) {
        val cursor = mAdapter.cursor

        if (cursor.moveToPosition(position)) {
            val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val title = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE))
            val subtitle = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE))

            val update = Feed(
                id = id,
                title = title + "Updated",
                subtitle = subtitle + "Updated"
            )

            UpdateQueryHandler(update).execute()
        }


    }

    /**
     * This class implements:
     * Command pattern (the execute() method)
     * Asynchronous Completion Token pattern (by virtue of inheriting from AsyncQueryHandler)
     */
    abstract inner class CompletionHandler : AsyncQueryHandler(contentResolver) {
        abstract fun execute()
    }


    inner class QueryQueryHandler : CompletionHandler() {
        override fun execute() {
            startQuery(0, null, MyContentProvider.CONTENT_URI, null, null, null, null)
        }

        override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor) {
            mAdapter.swapCursor(cursor)
        }
    }

    inner class InsertQueryHandler(val record: Feed, val nextCommand: Any) : CompletionHandler() {


        override fun execute() {
            val values = ContentValues()
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, record.title)
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, record.subtitle)

            /**
             * Invoke async insert operation on the CONTENT_URI
             */
            startInsert(0, nextCommand, MyContentProvider.CONTENT_URI, values)
        }

        override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {
            /**
             * Execute the next command when the async insert completes
             */
            (cookie as CompletionHandler).execute()
        }

    }

    inner class UpdateQueryHandler(val updateItem: Feed) : CompletionHandler() {
        override fun execute() {
            val v = ContentValues()
            v.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, updateItem.title)
            v.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, updateItem.subtitle)
            startUpdate(
                0,
                QueryQueryHandler(),
                Uri.withAppendedPath(MyContentProvider.CONTENT_URI, updateItem.id.toString()),
                v,
                null, null
            )
        }

        override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
            (cookie as CompletionHandler).execute()
        }

    }

    inner class DeleteQueryHandler(val deleteItem: Int) : CompletionHandler() {
        override fun execute() {
            startDelete(
                0,
                QueryQueryHandler(),
                Uri.withAppendedPath(MyContentProvider.CONTENT_URI, deleteItem.toString()),
                null, null
            )
        }

        override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
            (cookie as CompletionHandler).execute()
        }

    }


    companion object {
        /**
         * The loader's unique id
         */
        const val LOADER_ID = 0
    }

}
