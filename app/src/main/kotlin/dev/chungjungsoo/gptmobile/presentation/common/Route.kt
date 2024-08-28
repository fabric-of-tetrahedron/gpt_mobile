package dev.chungjungsoo.gptmobile.presentation.common

/**
 * 路由对象
 *
 * 此对象包含应用程序中所有导航路由的常量。
 * 这些常量用于在不同的屏幕和功能之间进行导航。
 */
object Route {

    /** 开始页面路由 */
    const val GET_STARTED = "get_started"

    /** 设置路由 */
    const val SETUP_ROUTE = "setup_route"
    /** 选择平台页面路由 */
    const val SELECT_PLATFORM = "select_platform"
    /** 令牌输入页面路由 */
    const val TOKEN_INPUT = "token_input"
    /** OpenAI模型选择页面路由 */
    const val OPENAI_MODEL_SELECT = "openai_model_select"
    /** Anthropic模型选择页面路由 */
    const val ANTHROPIC_MODEL_SELECT = "anthropic_model_select"
    /** Google模型选择页面路由 */
    const val GOOGLE_MODEL_SELECT = "google_model_select"
    /** Ollama模型选择页面路由 */
    const val OLLAMA_MODEL_SELECT = "ollama_model_select"

    /** 设置完成页面路由 */
    const val SETUP_COMPLETE = "setup_complete"

    /** 聊天列表页面路由 */
    const val CHAT_LIST = "chat_list"
    /**
     * 聊天室页面路由
     * 包含聊天室ID参数和已启用平台参数
     */
    const val CHAT_ROOM = "chat_room/{chatRoomId}?enabled={enabledPlatforms}"

    /** 设置路由 */
    const val SETTING_ROUTE = "setting_route"
    /** 通用设置页面路由 */
    const val SETTINGS = "settings"
    /** OpenAI设置页面路由 */
    const val OPENAI_SETTINGS = "openai_settings"
    /** Anthropic设置页面路由 */
    const val ANTHROPIC_SETTINGS = "anthropic_settings"
    /** Google设置页面路由 */
    const val GOOGLE_SETTINGS = "google_settings"
    /** Ollama设置页面路由 */
    const val OLLAMA_SETTINGS = "ollama_settings"

    /** 关于页面路由 */
    const val ABOUT_PAGE = "about"
    /** 许可证页面路由 */
    const val LICENSE = "license"
}
