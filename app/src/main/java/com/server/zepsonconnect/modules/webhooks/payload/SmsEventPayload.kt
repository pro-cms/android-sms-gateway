package com.server.zepsonconnect.modules.webhooks.payload

import java.util.Date

sealed class SmsEventPayload(
    val messageId: String,
    val phoneNumber: String,
    val simNumber: Int?,
) {
    class SmsSent(
        messageId: String,
        phoneNumber: String,
        simNumber: Int?,
        val sentAt: Date,
    ) : SmsEventPayload(messageId, phoneNumber, simNumber)

    class SmsDelivered(
        messageId: String,
        phoneNumber: String,
        simNumber: Int?,
        val deliveredAt: Date,
    ) : SmsEventPayload(messageId, phoneNumber, simNumber)

    class SmsFailed(
        messageId: String,
        phoneNumber: String,
        simNumber: Int?,
        val failedAt: Date,
        val reason: String,
    ) : SmsEventPayload(messageId, phoneNumber, simNumber)

    class SmsReceived(
        messageId: String,
        phoneNumber: String,
        simNumber: Int?,
        val message: String,
        val receivedAt: Date,
    ) : SmsEventPayload(messageId, phoneNumber, simNumber)

    class SmsDataReceived(
        messageId: String,
        phoneNumber: String,
        simNumber: Int?,
        val data: String,
        val receivedAt: Date,
    ) : SmsEventPayload(messageId, phoneNumber, simNumber)
}