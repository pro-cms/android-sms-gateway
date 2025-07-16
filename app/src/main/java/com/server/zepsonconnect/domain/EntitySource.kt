package com.server.zepsonconnect.domain

enum class EntitySource {
    Local,
    Cloud,

    @Deprecated("Not used anymore")
    Gateway,
}