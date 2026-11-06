package com.kyrdax.sitehub

import android.content.Context

@Suppress("StaticFieldLeak")
object AppContext {
    lateinit var instance: Context private set

    fun init(context: Context) {
        instance = context.applicationContext
    }
}