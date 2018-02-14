package com.stasbar.concurrency

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.stasbar.concurrency.bounded.local.BoundLocalActivity
import com.stasbar.concurrency.bounded.messenger.BoundViaMessengerActivity
import com.stasbar.concurrency.caching.CacheActivity
import com.stasbar.concurrency.contentProvider.ContactProviderDemoActivity
import com.stasbar.concurrency.contentResolver.ContentResolverActivity
import com.stasbar.concurrency.database.DBActivity
import com.stasbar.concurrency.java_concurrency.JavaConcurencyActivity
import com.stasbar.concurrency.java_concurrency.ProducerConsumerActivity
import com.stasbar.concurrency.started.StartedServicesActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonStartedImageDownloader.setOnClickListener { startActivity(Intent(this, DownloadActivity::class.java)) }
        buttonStartedServices.setOnClickListener { startActivity(Intent(this, StartedServicesActivity::class.java)) }
        buttonBoundedLocalService.setOnClickListener { startActivity(Intent(this, BoundLocalActivity::class.java)) }
        buttonBoundedViaMessenger.setOnClickListener { startActivity(Intent(this, BoundViaMessengerActivity::class.java))}
        buttonCache.setOnClickListener { startActivity(Intent(this, CacheActivity::class.java))}
        buttonJavaConcurrency.setOnClickListener{ startActivity(Intent(this, JavaConcurencyActivity::class.java)) }
        buttonProducerConsumer.setOnClickListener{ startActivity(Intent(this, ProducerConsumerActivity::class.java)) }
        buttonDatabase.setOnClickListener{ startActivity(Intent(this, DBActivity::class.java)) }
        buttonContentResolver.setOnClickListener{ startActivity(Intent(this, ContentResolverActivity::class.java)) }
        buttonContentProvider.setOnClickListener{ startActivity(Intent(this, ContactProviderDemoActivity::class.java)) }



    }
}
