package com.server.zepsonconnect.modules.messages.repositories

import androidx.lifecycle.distinctUntilChanged
import com.google.gson.GsonBuilder
import com.server.zepsonconnect.data.dao.MessagesDao
import com.server.zepsonconnect.data.entities.Message
import com.server.zepsonconnect.data.entities.MessageRecipient
import com.server.zepsonconnect.data.entities.MessageType
import com.server.zepsonconnect.data.entities.MessageWithRecipients
import com.server.zepsonconnect.domain.MessageContent
import com.server.zepsonconnect.domain.ProcessingState
import com.server.zepsonconnect.modules.messages.data.SendParams
import com.server.zepsonconnect.modules.messages.data.SendRequest
import com.server.zepsonconnect.modules.messages.data.StoredSendRequest
import java.util.Date

class MessagesRepository(private val dao: MessagesDao) {
    private val gson = GsonBuilder().serializeNulls().create()

    val lastMessages = dao.selectLast().distinctUntilChanged()

    fun get(id: String): StoredSendRequest {
        return dao.get(id)?.toRequest()
            ?: throw IllegalArgumentException("Message with id $id not found")
    }

    fun enqueue(request: SendRequest) {
        val message = MessageWithRecipients(
            Message(
                id = request.message.id,
                type = when (request.message.content) {
                    is MessageContent.Text -> MessageType.Text
                    is MessageContent.Data -> MessageType.Data
                },
                content = gson.toJson(request.message.content),
                withDeliveryReport = request.params.withDeliveryReport,
                simNumber = request.params.simNumber,
                validUntil = request.params.validUntil,
                isEncrypted = request.message.isEncrypted,
                skipPhoneValidation = request.params.skipPhoneValidation,
                priority = request.params.priority ?: Message.PRIORITY_DEFAULT,
                source = request.source,

                createdAt = request.message.createdAt.time,
            ),
            request.message.phoneNumbers.map {
                MessageRecipient(
                    request.message.id,
                    it,
                    ProcessingState.Pending
                )
            },
        )

        dao.insert(message)
    }

    fun getPending(): StoredSendRequest? {
        val message = dao.getPending() ?: return null

        if (message.state != ProcessingState.Pending) {
            // if for some reason stored state is not in sync with recipients state
            dao.updateMessageState(message.message.id, message.state)
            return getPending()
        }

        return message.toRequest()
    }

    private fun MessageWithRecipients.toRequest(): StoredSendRequest {
        val message = this

        return StoredSendRequest(
            id = message.rowId,
            state = message.state,
            recipients = this.recipients,
            message.message.source,
            com.server.zepsonconnect.modules.messages.data.Message(
                id = message.message.id,
                content = when (message.message.type) {
                    MessageType.Text -> gson.fromJson(
                        message.message.content,
                        MessageContent.Text::class.java
                    )

                    MessageType.Data -> gson.fromJson(
                        message.message.content,
                        MessageContent.Data::class.java
                    )
                },
                phoneNumbers = message.recipients.filter { it.state == ProcessingState.Pending }
                    .map { it.phoneNumber },
                isEncrypted = message.message.isEncrypted,
                createdAt = Date(message.message.createdAt),
            ),
            SendParams(
                withDeliveryReport = message.message.withDeliveryReport,
                skipPhoneValidation = message.message.skipPhoneValidation,
                simNumber = message.message.simNumber,
                validUntil = message.message.validUntil,
                priority = message.message.priority
            ),
        )
    }
}