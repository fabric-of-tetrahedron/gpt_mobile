package dev.chungjungsoo.gptmobile.data

import dev.chungjungsoo.gptmobile.data.model.ApiType

object ModelConstants {
    // LinkedHashSet should be used to guarantee item order
    val openaiModels = linkedSetOf("gpt-4o", "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo")
    val anthropicModels = linkedSetOf("claude-3-5-sonnet-20240620", "claude-3-opus-20240229", "claude-3-sonnet-20240229", "claude-3-haiku-20240307")
    val googleModels = linkedSetOf("gemini-1.5-pro-latest", "gemini-1.5-flash-latest", "gemini-1.0-pro")
    val ollamaModels = linkedSetOf("yi", "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo")

    const val OPENAI_API_URL = "https://api.openai.com"
    const val ANTHROPIC_API_URL = "https://api.anthropic.com"
    const val GOOGLE_API_URL = "https://generativelanguage.googleapis.com"
    const val OLLAMA_API_URL = "https://127.0.0.1:11434/v1"

    fun getDefaultAPIUrl(apiType: ApiType) = when (apiType) {
        ApiType.OPENAI -> OPENAI_API_URL
        ApiType.ANTHROPIC -> ANTHROPIC_API_URL
        ApiType.GOOGLE -> GOOGLE_API_URL
        ApiType.OLLAMA -> OLLAMA_API_URL
    }

    const val ANTHROPIC_MAXIMUM_TOKEN = 4096

    const val OPENAI_PROMPT =
        "You are a helpful, clever, and very friendly assistant. " +
            "You are familiar with various languages in the world. " +
            "You are to answer my questions precisely. "

    const val DEFAULT_PROMPT = "Your task is to answer my questions precisely."
}
