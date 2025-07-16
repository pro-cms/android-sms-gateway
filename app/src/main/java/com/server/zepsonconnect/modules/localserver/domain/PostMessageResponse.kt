package com.server.zepsonconnect.modules.localserver.domain

import com.server.zepsonconnect.domain.ProcessingState
import java.util.Date

data class PostMessageResponse(
    val id: String,
    val deviceId: String,
    val state: ProcessingState,
    val recipients: List<Recipient>,
    val isEncrypted: Boolean,
    val states: Map<ProcessingState, Date>
) {

    data class Recipient(
        val phoneNumber: String,
        val state: ProcessingState,
        val error: String?
    )
}
