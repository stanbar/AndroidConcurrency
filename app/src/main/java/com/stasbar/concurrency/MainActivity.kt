package com.stasbar.concurrency

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.benchmarks.ProofOfWorkActivity
import com.stasbar.concurrency.bounded.local.BoundLocalActivity
import com.stasbar.concurrency.bounded.messenger.BoundViaMessengerActivity
import com.stasbar.concurrency.caching.CacheActivity
import com.stasbar.concurrency.completablefuture.CompletableFutureActivity
import com.stasbar.concurrency.contentProvider.async.ContactProviderAsyncActivity
import com.stasbar.concurrency.contentProvider.sync.ContactProviderSyncActivity
import com.stasbar.concurrency.contentResolver.ContentResolverActivity
import com.stasbar.concurrency.database.DBActivity
import com.stasbar.concurrency.executor.ExecutorActivity
import com.stasbar.concurrency.java_concurrency.JavaConcurencyActivity
import com.stasbar.concurrency.java_concurrency.ProducerConsumerActivity
import com.stasbar.concurrency.looper.LooperActivity
import com.stasbar.concurrency.multiprocess.ProcessOneActivity
import com.stasbar.concurrency.multiprocess.ProcessTwoActivity
import com.stasbar.concurrency.piping.PipeActivity
import com.stasbar.concurrency.started.StartedServicesActivity
import com.stasbar.concurrency.threadsocket.LocalSocketActivity
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val buttons = mutableListOf(
            Button(this).apply {
                text = "ImageDownloader"
                setOnClickListener { startActivity<DownloadActivity>() }
            },
            Button(this).apply {
                text = "Started Service"
                setOnClickListener { startActivity<StartedServicesActivity>() }
            },
            Button(this).apply {
                text = "Bound Service"
                setOnClickListener { startActivity<BoundLocalActivity>() }
            },
            Button(this).apply {
                text = "Bounded via Messenger"
                setOnClickListener { startActivity<BoundViaMessengerActivity>() }
            },
            Button(this).apply {
                text = "Cache"
                setOnClickListener { startActivity<CacheActivity>() }
            },
            Button(this).apply {
                text = "Java Concurrency"
                setOnClickListener { startActivity<JavaConcurencyActivity>() }
            },
            Button(this).apply {
                text = "Producer Consumer"
                setOnClickListener { startActivity<ProducerConsumerActivity>() }
            },
            Button(this).apply {
                text = "Database"
                setOnClickListener { startActivity<DBActivity>() }
            },
            Button(this).apply {
                text = "Content Resolver"
                setOnClickListener { startActivity<ContentResolverActivity>() }
            },
            Button(this).apply {
                text = "Content Provider Sync"
                setOnClickListener { startActivity<ContactProviderSyncActivity>() }
            },
            Button(this).apply {
                text = "Content Provider Async"
                setOnClickListener { startActivity<ContactProviderAsyncActivity>() }
            },
            Button(this).apply {
                text = "Local socket"
                setOnClickListener { startActivity<LocalSocketActivity>() }
            },
            Button(this).apply {
                text = "Main Process"
                setOnClickListener { startActivity<ProcessOneActivity>() }
            },
            Button(this).apply {
                text = "Second Process"
                setOnClickListener { startActivity<ProcessTwoActivity>() }
            },
            Button(this).apply {
                text = "Proof of Work"
                setOnClickListener { startActivity<ProofOfWorkActivity>() }
            },
            Button(this).apply {
                text = "Pipe Activity"
                setOnClickListener { startActivity<PipeActivity>() }
            },
            Button(this).apply {
                text = "Looper Activity"
                setOnClickListener { startActivity<LooperActivity>() }
            },
            Button(this).apply {
                text = "Executor Activity"
                setOnClickListener { startActivity<ExecutorActivity>() }
            },
            Button(this).apply {
                text = "Completable Futures"
                setOnClickListener { startActivity<CompletableFutureActivity>() }
            }
        )
        buttons.forEach {
            container.addView(it)
        }
        root.addView(container)
        setContentView(root)
    }
}
