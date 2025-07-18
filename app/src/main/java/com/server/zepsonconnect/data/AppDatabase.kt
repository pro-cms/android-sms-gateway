package com.server.zepsonconnect.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.server.zepsonconnect.data.dao.MessagesDao
import com.server.zepsonconnect.data.entities.Message
import com.server.zepsonconnect.data.entities.MessageRecipient
import com.server.zepsonconnect.data.entities.MessageState
import com.server.zepsonconnect.data.entities.RecipientState
import com.server.zepsonconnect.modules.logs.db.LogEntriesDao
import com.server.zepsonconnect.modules.logs.db.LogEntry
import com.server.zepsonconnect.modules.webhooks.db.WebHook
import com.server.zepsonconnect.modules.webhooks.db.WebHooksDao

@Database(
    entities = [
        Message::class,
        MessageRecipient::class,
        RecipientState::class,
        MessageState::class,
        WebHook::class,
        LogEntry::class,
    ],
    version = 14,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
//        AutoMigration(from = 7, to = 8),  // manual migration
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
    abstract fun webhooksDao(): WebHooksDao
    abstract fun logDao(): LogEntriesDao

    companion object {
        fun getDatabase(context: android.content.Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "gateway"
            )
                .addMigrations(
                    MIGRATION_7_8,
                    MIGRATION_13_14,
                )
                .allowMainThreadQueries()
                .build()
        }
    }
}