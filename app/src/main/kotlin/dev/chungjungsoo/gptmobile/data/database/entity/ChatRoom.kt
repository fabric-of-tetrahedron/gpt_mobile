package dev.chungjungsoo.gptmobile.data.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import dev.chungjungsoo.gptmobile.data.model.ApiType
import kotlinx.parcelize.Parcelize

/**
 * ChatRoom 数据类
 *
 * 这个类代表一个聊天室实体，用于在数据库中存储聊天室信息。
 * 它使用了 Room 数据库的注解，并实现了 Parcelable 接口以支持数据传递。
 *
 * @property id 聊天室的唯一标识符，自动生成
 * @property title 聊天室的标题
 * @property enabledPlatform 启用的 API 平台列表
 * @property createdAt 聊天室创建的时间戳（以秒为单位）
 */
@Parcelize
@Entity(tableName = "chats")
data class ChatRoom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chat_id")
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "enabled_platform")
    val enabledPlatform: List<ApiType>,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() / 1000
) : Parcelable

/**
 * APITypeConverter 类
 *
 * 这个类用于在 Room 数据库中转换 ApiType 列表和字符串之间的数据。
 * 它提供了两个方法来实现这种转换。
 */
class APITypeConverter {
    /**
     * 将字符串转换为 ApiType 列表
     *
     * @param value 包含 ApiType 名称的字符串，以逗号分隔
     * @return ApiType 对象的列表
     */
    @TypeConverter
    fun fromString(value: String): List<ApiType> {
        val splitted = value.split(',')

        return splitted.map { s -> ApiType.valueOf(s) }
    }

    /**
     * 将 ApiType 列表转换为字符串
     *
     * @param value ApiType 对象的列表
     * @return 包含 ApiType 名称的字符串，以逗号分隔
     */
    @TypeConverter
    fun fromList(value: List<ApiType>): String = value.joinToString(",") { v -> v.name }
}
