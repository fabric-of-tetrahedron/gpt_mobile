package dev.chungjungsoo.gptmobile.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.chungjungsoo.gptmobile.data.model.ApiType

/**
 * 消息实体类
 * 用于表示聊天中的单条消息
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatRoom::class,
            parentColumns = ["chat_id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Message(
    /** 消息ID，主键，自动生成 */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("message_id")
    val id: Int = 0,

    /** 关联的聊天室ID */
    @ColumnInfo(name = "chat_id")
    val chatId: Int = 0,

    /** 消息内容 */
    @ColumnInfo(name = "content")
    val content: String,

    /** 图片数据，可为空 */
    @ColumnInfo(name = "image_data")
    val imageData: String? = null,

    /** 关联消息ID，用于回复等功能 */
    @ColumnInfo(name = "linked_message_id")
    val linkedMessageId: Int = 0,

    /** 平台类型，表示消息来源的API类型 */
    @ColumnInfo(name = "platform_type")
    val platformType: ApiType?,

    /** 消息创建时间，以Unix时间戳形式存储 */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() / 1000
)
