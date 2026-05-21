package com.smartkdfarm.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform