package com.stasbar.concurrency.contentResolver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_content_resolver.*

class ContentResolverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_resolver)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
            return
        }

        manually()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            manually()

    }

    fun manually() {
        val contacts = ArrayList<String>()
        val columns = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                columns, null, null, null)


        if (cursor.moveToFirst()) {
            do {
                val columnName = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                val columnIndex = cursor.getColumnIndex(columnName)
                val contactName = cursor.getString(columnIndex)
                contacts.add(contactName)
            } while (cursor.moveToNext())
        }

        cursor.close()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts)
        setListAdapter(adapter)
    }

    private fun setListAdapter(adapter: BaseAdapter) {
        listView.adapter = adapter
    }
}
