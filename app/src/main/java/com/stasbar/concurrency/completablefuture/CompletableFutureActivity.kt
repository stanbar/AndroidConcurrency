package com.stasbar.concurrency.completablefuture

import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/*
# CompletableFuture factories:

supplyAsync :: () -> CompletableFuture<R>
runAsync :: () -> CompletableFuture<Void>


# CompletableFuture .then chaining API cheatsheet:

thenApply :: CompletableFuture: (T) -> R
it acts like transformation/map function

thenAccept :: CompletableFuture: (T) -> Void
it get's result of completed future but doesn't return anything

thenRun :: CompletableFeature: () -> Void
no argument and no result, its more like a finisher

# Combining futures:

thenApply :: (CompletableFuture: () -> T) -> (CompletableFuture: (T) -> R) ->  CompletableFuture<CompletableFuture<R>>
creates depending chain of completable futures while the resulting future will be of type R

Combining two dependent tasks using compose

thenCompose :: (CompletableFuture: () -> T) -> (CompletableFuture: (T) -> R) -> R
creates depending chain of completable futures while the resulting future will be of type R

Combine two independent futures using combine


*/
class CompletableFutureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        setContentView(root)
        Button(this).apply {
            root.addView(this)
            text = "Future Chain"
            setOnClickListener {
                val start = System.currentTimeMillis()
                val executor = Executors.newFixedThreadPool(5)
                val result1 = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    "hello"
                })
                val capitalized1 = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    val result = result1.get().capitalize()
                    result
                })
                val result2 = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    "world"
                })
                val capitalized2 = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    val result = result2.get().capitalize()
                    result
                })
                val concated = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    val result = capitalized1.get() + " " + capitalized2.get()
                    result
                })
                val shouted = executor.submit(Callable<String> {
                    SystemClock.sleep(500)
                    val result = "${concated.get()}!"
                    result
                })
                executor.execute {
                    SystemClock.sleep(500)
                    Timber.d(shouted.get())
                    Timber.d("Future chain took ${(System.currentTimeMillis() - start) / 1000.0} sec")
                }
            }
        }
        Button(this).apply {
            root.addView(this)
            text = "CompletableFuture chain"
            setOnClickListener {
                val start = System.currentTimeMillis()
                val async1 = CompletableFuture.supplyAsync {
                    SystemClock.sleep(500)
                    "hello"
                }.thenCompose {
                    CompletableFuture.supplyAsync {
                        SystemClock.sleep(500)
                        it.capitalize()
                    }
                }

                val async2 = CompletableFuture.supplyAsync {
                    SystemClock.sleep(500)
                    "world"
                }.thenCompose {
                    CompletableFuture.supplyAsync {
                        SystemClock.sleep(500)
                        it.capitalize()
                    }
                }

                async1.thenCombine(async2) { first: String, second: String ->
                    SystemClock.sleep(500)
                    first + second
                }.thenApply {
                    SystemClock.sleep(500)
                    "$it!"
                }.thenAccept {
                    SystemClock.sleep(500)
                    Timber.d(it)
                    Timber.d("CompletableFuture chain took ${(System.currentTimeMillis() - start) / 1000.0} sec")
                }
            }
        }
    }
}
