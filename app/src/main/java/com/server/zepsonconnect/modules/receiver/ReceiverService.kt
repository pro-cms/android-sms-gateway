package com.server.zepsonconnect.modules.receiver

import android.content.Context
import android.os.Build
import android.provider.Telephony
import android.util.Base64
import com.server.zepsonconnect.helpers.SubscriptionsHelper
import com.server.zepsonconnect.modules.logs.LogsService
import com.server.zepsonconnect.modules.logs.db.LogEntry
import com.server.zepsonconnect.modules.receiver.data.InboxMessage
import com.server.zepsonconnect.modules.webhooks.WebHooksService
import com.server.zepsonconnect.modules.webhooks.domain.WebHookEvent
import com.server.zepsonconnect.modules.webhooks.payload.SmsEventPayload
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date

class ReceiverService : KoinComponent {
    private val webHooksService: WebHooksService by inject()
    private val logsService: LogsService by inject()

    fun export(context: Context, period: Pair<Date, Date>) {
        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::export - start",
            mapOf("period" to period)
        )

        select(context, period)
            .forEach {
                process(context, it)
            }

        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::export - end",
            mapOf("period" to period)
        )
    }

    fun process(context: Context, message: InboxMessage) {
        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::process - message received",
            mapOf("message" to message)
        )

        val (type, payload) = when (message) {
            is InboxMessage.Text -> WebHookEvent.SmsReceived to SmsEventPayload.SmsReceived(
                messageId = message.hashCode().toUInt().toString(16),
                message = message.text,
                phoneNumber = message.address,
                simNumber = message.subscriptionId?.let {
                    SubscriptionsHelper.getSimSlotIndex(
                        context,
                        it
                    )
                }?.let { it + 1 },
                receivedAt = message.date,
            )

            is InboxMessage.Data -> WebHookEvent.SmsDataReceived to SmsEventPayload.SmsDataReceived(
                messageId = message.hashCode().toUInt().toString(16),
                data = Base64.encodeToString(message.data, Base64.NO_WRAP),
                phoneNumber = message.address,
                simNumber = message.subscriptionId?.let {
                    SubscriptionsHelper.getSimSlotIndex(
                        context,
                        it
                    )
                }?.let { it + 1 },
                receivedAt = message.date,
            )
        }

        webHooksService.emit(type, payload)

        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::process - message processed",
            mapOf("type" to type, "payload" to payload)
        )
    }

    fun select(context: Context, period: Pair<Date, Date>): List<InboxMessage> {
        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::select - start",
            mapOf("period" to period)
        )

        val projection = mutableListOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.DATE,
            Telephony.Sms.BODY,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            projection += Telephony.Sms.SUBSCRIPTION_ID
        }

        val selection = "${Telephony.Sms.DATE} >= ? AND ${Telephony.Sms.DATE} <= ?"
        val selectionArgs = arrayOf(
            period.first.time.toString(),
            period.second.time.toString()
        )
        val sortOrder = Telephony.Sms.DATE

        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )

        val messages = mutableListOf<InboxMessage>()

        cursor?.use { cursor ->
            while (cursor.moveToNext()) {
                messages.add(
                    InboxMessage.Text(
                        address = cursor.getString(1),
                        date = Date(cursor.getLong(2)),
                        text = cursor.getString(3),
                        subscriptionId = when {
                            projection.size > 4 -> cursor.getInt(4).takeIf { it >= 0 }
                            else -> null
                        }
                    )
                )
            }
        }

        logsService.insert(
            LogEntry.Priority.DEBUG,
            MODULE_NAME,
            "ReceiverService::select - end",
            mapOf("messages" to messages.size)
        )

        return messages
    }
}