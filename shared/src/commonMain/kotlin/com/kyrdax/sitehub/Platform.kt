package com.kyrdax.sitehub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform