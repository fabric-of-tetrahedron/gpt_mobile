package dev.chungjungsoo.gptmobile.data.dto.anthropic.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 消息请求数据类
 *
 * 该类用于封装发送给Anthropic API的消息请求。
 * 包含了模型、消息内容、最大令牌数等参数。
 *
 * 注意：当某些值在将来使用时，使用 @EncodeDefault 或移除默认值
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MessageRequest(
    /**
     * 使用的模型名称
     */
    @SerialName("model")
    val model: String,

    /**
     * 输入消息列表
     */
    @SerialName("messages")
    val messages: List<InputMessage>,

    /**
     * 生成响应的最大令牌数
     */
    @SerialName("max_tokens")
    val maxTokens: Int,

    /**
     * 请求元数据，可选
     */
    @SerialName("metadata")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val metadata: RequestMetadata? = null,

    /**
     * 停止序列，可用于提前结束生成，可选
     */
    @SerialName("stop_sequences")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val stopSequences: List<String>? = null,

    /**
     * 是否启用流式传输，默认为false
     */
    @SerialName("stream")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val stream: Boolean = false,

    /**
     * 系统提示，用于设置AI助手的行为或角色，可选
     */
    @SerialName("system")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val systemPrompt: String? = null,

    /**
     * 温度参数，控制输出的随机性，可选
     */
    @SerialName("temperature")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val temperature: Float? = null,

    /**
     * Top-K采样参数，可选
     */
    @SerialName("top_k")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val topK: Int? = null,

    /**
     * Top-P（核采样）参数，可选
     */
    @SerialName("top_p")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val topP: Float? = null
)
