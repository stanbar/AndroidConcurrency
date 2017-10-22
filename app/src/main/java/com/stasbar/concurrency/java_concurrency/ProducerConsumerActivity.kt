package com.stasbar.concurrency.java_concurrency

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.stasbar.concurrency.LoggableActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.data.Transacton
import kotlinx.android.synthetic.main.activity_producer_consumer.*
import java.math.BigDecimal
import java.util.*

class ProducerConsumerActivity : LoggableActivity() {
    override fun getLogger() = logger
    val queue: LinkedList<Transacton> = object : LinkedList<Transacton>() {
        override fun addLast(e: Transacton?) {
            super.addLast(e)
            synchronized(this) {
                (this as Object).notifyAll()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producer_consumer)

        val producer = Thread({
            val rand = Random()
            while (true) {
                val transcation = Transacton(UUID.randomUUID().toString(), UUID.randomUUID().toString(), BigDecimal(rand.nextDouble()))
                queue.addLast(transcation)
                Thread.sleep(1000)
            }
        })

        val consume = {
            val handler = Handler(Looper.getMainLooper())
            while (true) {
                var transction : Transacton? = null
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        log("Waiting for new product")
                        (queue as Object).wait()
                    }
                    transction = queue.remove()
                }

                log("Processing ${transction!!.hashCode()}")
                Thread.sleep(700)
                log("Done ${transction!!.hashCode()}")
            }
        }
        val consumer1 = Thread(consume)
        val consumer2 = Thread(consume)

        consumer1.name = "Consumer-1"
        consumer2.name = "Consumer-2"

        consumer1.start()
        consumer2.start()

        producer.start()

    }

}
