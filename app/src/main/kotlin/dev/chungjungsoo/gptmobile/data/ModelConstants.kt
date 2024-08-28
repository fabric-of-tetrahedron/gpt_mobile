package dev.chungjungsoo.gptmobile.data

import dev.chungjungsoo.gptmobile.data.model.ApiType

/**
 * 模型常量对象
 * 包含了各种AI模型的相关常量和配置
 */
object ModelConstants {
    // LinkedHashSet应该被用来保证项目顺序

    /** OpenAI模型列表 */
    val openaiModels = linkedSetOf("gpt-4o", "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo")

    /** Anthropic模型列表 */
    val anthropicModels = linkedSetOf("claude-3-5-sonnet-20240620", "claude-3-opus-20240229", "claude-3-sonnet-20240229", "claude-3-haiku-20240307")

    /** Google模型列表 */
    val googleModels = linkedSetOf("gemini-1.5-pro-latest", "gemini-1.5-flash-latest", "gemini-1.0-pro")

    /** Ollama模型列表 */
    val ollamaModels = linkedSetOf<String>()

    /** Ollama模型描述映射 */
    val ollamaModelDescriptions = hashMapOf<String,String>()

    /** OpenAI API的URL */
    const val OPENAI_API_URL = "https://api.openai.com"

    /** Anthropic API的URL */
    const val ANTHROPIC_API_URL = "https://api.anthropic.com"

    /** Google API的URL */
    const val GOOGLE_API_URL = "https://generativelanguage.googleapis.com"

    /** Ollama API的URL */
    const val OLLAMA_API_URL = "http://127.0.0.1:11434/v1"

    /**
     * 根据API类型获取默认的API URL
     * @param apiType API类型
     * @return 对应的API URL
     */
    fun getDefaultAPIUrl(apiType: ApiType) = when (apiType) {
        ApiType.OPENAI -> OPENAI_API_URL
        ApiType.ANTHROPIC -> ANTHROPIC_API_URL
        ApiType.GOOGLE -> GOOGLE_API_URL
        ApiType.OLLAMA -> OLLAMA_API_URL
    }

    /** Anthropic模型的最大token数 */
    const val ANTHROPIC_MAXIMUM_TOKEN = 4096

    /** OpenAI模型的默认提示词 */
    const val OPENAI_PROMPT =
        "You are a helpful, clever, and very friendly assistant. " +
            "You are familiar with various languages in the world. " +
            "You are to answer my questions precisely. "

    /** 默认提示词 */
    const val DEFAULT_PROMPT = "Your task is to answer my questions precisely."
}
