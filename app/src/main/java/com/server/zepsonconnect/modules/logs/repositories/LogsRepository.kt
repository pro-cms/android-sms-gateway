package com.server.zepsonconnect.modules.logs.repositories

import androidx.lifecycle.distinctUntilChanged
import com.server.zepsonconnect.modules.logs.db.LogEntriesDao

class LogsRepository(
    private val dao: LogEntriesDao
) {
    val lastEntries = dao.selectLast().distinctUntilChanged()
}