package com.stasbar.concurrency.contentProvider

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.SimpleCursorAdapter
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_contact_provider_demo.*
import kotlinx.android.synthetic.main.content_contact_provider_demo.*


class ContactProviderDemoActivity : AppCompatActivity() {
    private lateinit var cr: ContentResolver
    private lateinit var client: ContentProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_provider_demo)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        cr = contentResolver
        inserts()
        deletes()
        update()
        query()

    }


    private fun inserts() {
        cr.acquireContentProviderClient(MyCP.CONTENT_URI).use {
            val values = ContentValues()

            values.put("data", "Record1")
            cr.insert(MyCP.CONTENT_URI, values);

            values.clear()
            values.put("data", "Record2")
            cr.insert(MyCP.CONTENT_URI, values);

            values.clear()
            values.put("data", "Record3")
            cr.insert(MyCP.CONTENT_URI, values)
        }
    }

    private fun deletes() {
        cr.acquireContentProviderClient(MyCP.CONTENT_URI).use { it.delete(Uri.withAppendedPath(MyCP.CONTENT_URI, "1"), null, null) }
    }

    private fun update() {
        cr.acquireContentProviderClient(MyCP.CONTENT_URI).use {
            val values = ContentValues()
            values.put(MyCP.DATA, "RecordUpdated")
            it.update(Uri.withAppendedPath(MyCP.CONTENT_URI, "2"), values, null, null)
        }


    }

    private fun query() {
        cr.acquireContentProviderClient(MyCP.CONTENT_URI).query(MyCP.CONTENT_URI, null, null, null, null).use {
            val items = ArrayList<String>()
            if (it.moveToFirst()) {
                do {
                    val idIndex = it.getColumnIndex(MyCP._ID)
                    val id = it.getString(idIndex)
                    val dataIndex = it.getColumnIndex(MyCP.DATA)
                    val data = it.getString(dataIndex)
                    items.add(id + " " + data)
                } while (it.moveToNext())
            }
            val adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, items)
            setListAdapter(adapter)
        }
    }

    private fun setListAdapter(adapter: BaseAdapter) {
        listViewContentProvider.adapter = adapter
    }

}
