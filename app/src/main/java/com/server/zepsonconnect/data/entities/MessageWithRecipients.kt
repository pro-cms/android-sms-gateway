package com.server.zepsonconnect.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

data class MessageWithRecipients(
    @Embedded val message: Message,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId",
    )
    val recipients: List<MessageRecipient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId",
    )
    val states: List<MessageState> = emptyList(),
    @ColumnInfo(name = "rowid")
    val rowId: Long = 0,
) {
    val state: com.server.zepsonconnect.domain.ProcessingState
        get() = when {
            recipients.any { it.state == com.server.zepsonconnect.domain.ProcessingState.Pending } -> com.server.zepsonconnect.domain.ProcessingState.Pending
            recipients.any { it.state == com.server.zepsonconnect.domain.ProcessingState.Processed } -> com.server.zepsonconnect.domain.ProcessingState.Processed

            recipients.all { it.state == com.server.zepsonconnect.domain.ProcessingState.Failed } -> com.server.zepsonconnect.domain.ProcessingState.Failed
            recipients.all { it.state == com.server.zepsonconnect.domain.ProcessingState.Delivered } -> com.server.zepsonconnect.domain.ProcessingState.Delivered
            else -> com.server.zepsonconnect.domain.ProcessingState.Sent
        }
}
