package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 停止原因枚举类
 *
 * 这个枚举类定义了Anthropic API响应中可能出现的停止原因。
 * 每个枚举值都对应一个特定的停止原因，并使用@SerialName注解指定了序列化时使用的名称。
 */
@Serializable
enum class StopReason {

    /**
     * 表示对话轮次结束
     */
    @SerialName("end_turn")
    END_TURN,

    /**
     * 表示达到了最大令牌数限制
     */
    @SerialName("max_tokens")
    MAX_TOKENS,

    /**
     * 表示遇到了停止序列
     */
    @SerialName("stop_sequence")
    STOP_SEQUENCE,

    /**
     * 表示使用了工具
     */
    @SerialName("tool_use")
    TOOL_USE
}
