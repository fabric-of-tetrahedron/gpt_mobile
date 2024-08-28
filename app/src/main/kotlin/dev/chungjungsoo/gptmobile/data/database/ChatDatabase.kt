package dev.chungjungsoo.gptmobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.chungjungsoo.gptmobile.data.database.dao.ChatRoomDao
import dev.chungjungsoo.gptmobile.data.database.dao.MessageDao
import dev.chungjungsoo.gptmobile.data.database.entity.APITypeConverter
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.database.entity.Message

/**
 * 聊天数据库类
 *
 * 这个类定义了应用程序的主要数据库结构，包含聊天室和消息实体。
 * 它使用Room持久性库来管理SQLite数据库操作。
 */
@Database(entities = [ChatRoom::class, Message::class], version = 1)
@TypeConverters(APITypeConverter::class)
abstract class ChatDatabase : RoomDatabase() {

    /** 获取聊天室数据访问对象 */
    abstract fun chatRoomDao(): ChatRoomDao

    /** 获取消息数据访问对象 */
    abstract fun messageDao(): MessageDao
}
