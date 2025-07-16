package com.server.zepsonconnect.modules.messages.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.server.zepsonconnect.data.entities.Message
import com.server.zepsonconnect.modules.messages.repositories.MessagesRepository

class MessagesListViewModel(
    messagesRepo: MessagesRepository
) : ViewModel() {
    val messages: LiveData<List<Message>> =
        messagesRepo.lastMessages
}