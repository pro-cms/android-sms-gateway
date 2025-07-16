package com.server.zepsonconnect.modules.health.domain

import com.google.gson.annotations.SerializedName

enum class Status {
    @SerializedName("pass")
    PASS,

    @SerializedName("warn")
    WARN,

    @SerializedName("fail")
    FAIL,
}