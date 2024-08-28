package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 事件类型枚举
 *
 * 该枚举类定义了Anthropic API响应中可能出现的各种事件类型。
 * 每个枚举值都对应一个特定的事件，用于标识API响应流中的不同阶段或状态。
 */
@Serializable
enum class EventType {

    /**
     * 消息开始事件
     * 表示一个新的消息开始
     */
    @SerialName("message_start")
    MESSAGE_START,

    /**
     * 内容块开始事件
     * 表示一个新的内容块开始
     */
    @SerialName("content_block_start")
    CONTENT_START,

    /**
     * 内容块增量事件
     * 表示内容块的部分更新或增量
     */
    @SerialName("content_block_delta")
    CONTENT_DELTA,

    /**
     * 内容块结束事件
     * 表示一个内容块的结束
     */
    @SerialName("content_block_stop")
    CONTENT_STOP,

    /**
     * 消息增量事件
     * 表示消息的部分更新或增量
     */
    @SerialName("message_delta")
    MESSAGE_DELTA,

    /**
     * 消息结束事件
     * 表示一个消息的结束
     */
    @SerialName("message_stop")
    MESSAGE_STOP,

    /**
     * 心跳事件
     * 用于保持连接活跃的周期性事件
     */
    @SerialName("ping")
    PING,

    /**
     * 错误事件
     * 表示在处理过程中发生了错误
     */
    @SerialName("error")
    ERROR
}
