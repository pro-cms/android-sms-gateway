package com.server.zepsonconnect.domain

enum class ProcessingState {
    Pending,
    Processed,
    Sent,
    Delivered,
    Failed
}