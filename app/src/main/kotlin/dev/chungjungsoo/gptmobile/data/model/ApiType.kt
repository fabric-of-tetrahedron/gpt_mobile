package dev.chungjungsoo.gptmobile.data.model

/**
 * API类型枚举
 *
 * 此枚举类定义了支持的不同API类型。
 * 每个枚举值代表一个特定的AI服务提供商。
 */
enum class ApiType {
    /**
     * OpenAI的API
     * 代表OpenAI公司提供的AI服务，如GPT系列模型
     */
    OPENAI,

    /**
     * Anthropic的API
     * 代表Anthropic公司提供的AI服务，如Claude模型
     */
    ANTHROPIC,

    /**
     * Google的API
     * 代表Google公司提供的AI服务，如PaLM模型
     */
    GOOGLE,

    /**
     * Ollama的API
     * 代表Ollama提供的本地运行的AI模型服务
     */
    OLLAMA
}
