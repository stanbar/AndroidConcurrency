package com.stasbar.concurrency

import java.util.*

/**
 * Created by stasbar on 27.07.2017
 */
class Square private constructor(builder: Builder) {
    private val width: Int
    private val height: Int
    private val area: Int
    private val key: String
    init {
        key = builder.key
        width = builder.width
        height = builder.height
        area = width * height
    }

    fun add(width: Int, height: Int): Square {
        return Builder(this.width + width, this.height + height).build()
    }

    override fun toString(): String {
        return "Square(width=$width, height=$height, area=$area, key='$key')"
    }

    class Builder(val width: Int, val height: Int) {
        var key : String = UUID.randomUUID().toString()
        fun build() = Square(this)
        fun build(key : String):Square{
            this.key = key
            return Square(this)
        }

    }



}