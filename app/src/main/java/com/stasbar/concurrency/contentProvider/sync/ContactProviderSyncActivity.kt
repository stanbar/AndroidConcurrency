package com.stasbar.concurrency.contentProvider.sync

import android.app.ListActivity
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.SimpleAdapter
import com.stasbar.concurrency.R
import com.stasbar.concurrency.contentProvider.Feed
import com.stasbar.concurrency.contentProvider.FeedReaderContract
import com.stasbar.concurrency.contentProvider.MyContentProvider
import java.util.*


class ContactProviderSyncActivity : ListActivity() {
    private lateinit var cr: ContentResolver
    private lateinit var adapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_provider)

        cr = contentResolver
        query()


        listView.setOnItemClickListener { adapter, view, position, id ->
            delete(position)
        }
        listView.setOnItemLongClickListener { adapter, view, position, id ->
            update(position)
            true
        }
    }


    fun query(view: View? = null) {
        cr.acquireContentProviderClient(MyContentProvider.CONTENT_URI)!!
            .query(MyContentProvider.CONTENT_URI, null, null, null, null)
            ?.use {
                val items = ArrayList<Map<String, Any>>()
                while (it.moveToNext()) {
                    val idIndex = it.getColumnIndex(BaseColumns._ID)
                    val id = it.getInt(idIndex)
                    val titleIndex =
                        it.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)
                    val title = it.getString(titleIndex)
                    val subTitleIndex =
                        it.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
                    val subTitle = it.getString(subTitleIndex)
                    val feed = Feed(id = id, title = title, subtitle = subTitle)
                    items.add(
                        mapOf(
                            Pair(BaseColumns._ID, feed.id),
                            Pair(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, feed.title)
                        )
                    )
                }
                val columns =
                    arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)
                val viewIds = intArrayOf(android.R.id.text1, android.R.id.text2)

                adapter = SimpleAdapter(
                    applicationContext,
                    items,
                    android.R.layout.simple_list_item_2,
                    columns,
                    viewIds
                )

                listAdapter = adapter
            }
    }

    fun insert(view: View? = null) {
        cr.acquireContentProviderClient(MyContentProvider.CONTENT_URI)?.use {
            val feed = Feed()
            val values = ContentValues().apply {
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, feed.title)
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, feed.subtitle)
            }
            cr.insert(MyContentProvider.CONTENT_URI, values)
        }
    }

    fun delete(position: Int) {
        cr.acquireContentProviderClient(MyContentProvider.CONTENT_URI)?.use {
            val id = if (adapter.getItem(position) is Map<*, *>)
                (adapter.getItem(position) as Map<String, Any>)[BaseColumns._ID] else return

            it.delete(
                Uri.withAppendedPath(MyContentProvider.CONTENT_URI, id.toString()),
                null,
                null
            )

        }
    }


    fun update(position: Int) {
        cr.acquireContentProviderClient(MyContentProvider.CONTENT_URI)?.use {
            if (adapter.getItem(position) !is Map<*, *>)
                return

            val map = adapter.getItem(position) as Map<String, Any>

            val id: Int = map[BaseColumns._ID] as Int
            val title: String = map[FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE] as String
            val subtitle: String = map[FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE] as String

            val feed = Feed(
                id = id,
                title = title + "Updated",
                subtitle = subtitle + "Updated"
            )


            val values = ContentValues().apply {
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, feed.title)
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, feed.subtitle)
            }
            it.update(
                Uri.withAppendedPath(MyContentProvider.CONTENT_URI, id.toString()),
                values,
                null,
                null
            )
        }


    }
}

