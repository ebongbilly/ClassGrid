package com.classgrid.main

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform