package com.server.zepsonconnect.domain

sealed class MessageContent {
    data class Text(val text: String) : MessageContent() {
        override fun toString(): String {
            return text
        }
    }

    data class Data(val data: String, val port: UShort) : MessageContent() {
        override fun toString(): String {
            return "$data:$port"
        }
    }
}