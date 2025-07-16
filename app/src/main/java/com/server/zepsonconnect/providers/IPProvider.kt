package com.server.zepsonconnect.providers

interface IPProvider {
    suspend fun getIP(): String?
}