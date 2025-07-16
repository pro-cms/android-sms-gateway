package com.server.zepsonconnect.modules.logs.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.server.zepsonconnect.modules.logs.db.LogEntry
import com.server.zepsonconnect.modules.logs.repositories.LogsRepository

class LogsViewModel(
    logs: LogsRepository
) : ViewModel() {
    val lastEntries: LiveData<List<LogEntry>> = logs.lastEntries
}