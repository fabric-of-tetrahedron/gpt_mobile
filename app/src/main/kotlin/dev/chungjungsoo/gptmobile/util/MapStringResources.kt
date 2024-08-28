package dev.chungjungsoo.gptmobile.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.dto.APIModel
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode

/**
 * 获取平台标题资源
 *
 * @return 返回一个包含各个API类型对应标题的Map
 */
@Composable
fun getPlatformTitleResources(): Map<ApiType, String> = mapOf(
    ApiType.OPENAI to stringResource(R.string.openai),
    ApiType.ANTHROPIC to stringResource(R.string.anthropic),
    ApiType.GOOGLE to stringResource(R.string.google),
    ApiType.OLLAMA to stringResource(R.string.ollama)
)

/**
 * 获取平台描述资源
 *
 * @return 返回一个包含各个API类型对应描述的Map
 */
@Composable
fun getPlatformDescriptionResources(): Map<ApiType, String> = mapOf(
    ApiType.OPENAI to stringResource(R.string.openai_description),
    ApiType.ANTHROPIC to stringResource(R.string.anthropic_description),
    ApiType.GOOGLE to stringResource(R.string.google_description),
    ApiType.OLLAMA to stringResource(R.string.ollama_description)
)

/**
 * 获取平台API标签资源
 *
 * @return 返回一个包含各个API类型对应API标签的Map
 */
@Composable
fun getPlatformAPILabelResources(): Map<ApiType, String> = mapOf(
    ApiType.OPENAI to stringResource(R.string.openai_api_key),
    ApiType.ANTHROPIC to stringResource(R.string.anthropic_api_key),
    ApiType.GOOGLE to stringResource(R.string.google_api_key),
    ApiType.OLLAMA to stringResource(R.string.ollama_api_key)
)

/**
 * 获取平台帮助链接资源
 *
 * @return 返回一个包含各个API类型对应帮助链接的Map
 */
@Composable
fun getPlatformHelpLinkResources(): Map<ApiType, String> = mapOf(
    ApiType.OPENAI to stringResource(R.string.openai_api_help),
    ApiType.ANTHROPIC to stringResource(R.string.anthropic_api_help),
    ApiType.GOOGLE to stringResource(R.string.google_api_help),
    ApiType.OLLAMA to stringResource(R.string.ollama_api_help)
)

/**
 * 生成OpenAI模型列表
 *
 * @param models OpenAI模型集合
 * @return 返回一个包含模型名称、描述和标识符的APIModel列表
 */
@Composable
fun generateOpenAIModelList(models: LinkedHashSet<String>) = models.mapIndexed { index, model ->
    val (name, description) = when (index) {
        0 -> stringResource(R.string.gpt_4o) to stringResource(R.string.gpt_4o_description)
        1 -> stringResource(R.string.gpt_4_turbo) to stringResource(R.string.gpt_4_turbo_description)
        2 -> stringResource(R.string.gpt_4) to stringResource(R.string.gpt_4_description)
        3 -> stringResource(R.string.gpt_3_5_turbo) to stringResource(R.string.gpt_3_5_description)
        else -> "" to ""
    }
    APIModel(name, description, model)
}

/**
 * 生成Ollama模型列表
 *
 * @param models Ollama模型集合
 * @param descriptions 模型描述的HashMap
 * @return 返回一个包含模型名称、描述和标识符的APIModel列表
 */
@Composable
fun generateOllamaModelList(models: LinkedHashSet<String>, descriptions: HashMap<String, String>) = models.mapIndexed { index, model ->
    val (name, description) = when {
        else -> model to (descriptions.get(model) ?: "")
    }
    APIModel(name, description, model)
}

/**
 * 生成Anthropic模型列表
 *
 * @param models Anthropic模型集合
 * @return 返回一个包含模型名称、描述和标识符的APIModel列表
 */
@Composable
fun generateAnthropicModelList(models: LinkedHashSet<String>) = models.mapIndexed { index, model ->
    val (name, description) = when (index) {
        0 -> stringResource(R.string.claude_3_5_sonnet) to stringResource(R.string.claude_3_5_sonnet_description)
        1 -> stringResource(R.string.claude_3_opus) to stringResource(R.string.claude_3_opus_description)
        2 -> stringResource(R.string.claude_3_sonnet) to stringResource(R.string.claude_3_sonnet_description)
        3 -> stringResource(R.string.claude_3_haiku) to stringResource(R.string.claude_3_haiku_description)
        else -> "" to ""
    }
    APIModel(name, description, model)
}

/**
 * 生成Google模型列表
 *
 * @param models Google模型集合
 * @return 返回一个包含模型名称、描述和标识符的APIModel列表
 */
@Composable
fun generateGoogleModelList(models: LinkedHashSet<String>) = models.mapIndexed { index, model ->
    val (name, description) = when (index) {
        0 -> stringResource(R.string.gemini_1_5_pro) to stringResource(R.string.gemini_1_5_pro_description)
        1 -> stringResource(R.string.gemini_1_5_flash) to stringResource(R.string.gemini_1_5_flash_description)
        2 -> stringResource(R.string.gemini_1_0_pro) to stringResource(R.string.gemini_1_0_pro_description)
        else -> "" to ""
    }
    APIModel(name, description, model)
}

/**
 * 获取API模型选择标题
 *
 * @param apiType API类型
 * @return 返回对应API类型的模型选择标题
 */
@Composable
fun getAPIModelSelectTitle(apiType: ApiType) = when (apiType) {
    ApiType.OPENAI -> stringResource(R.string.select_openai_model)
    ApiType.ANTHROPIC -> stringResource(R.string.select_anthropic_model)
    ApiType.GOOGLE -> stringResource(R.string.select_google_model)
    ApiType.OLLAMA -> stringResource(R.string.select_ollama_model)
}

/**
 * 获取API模型选择描述
 *
 * @param apiType API类型
 * @return 返回对应API类型的模型选择描述
 */
@Composable
fun getAPIModelSelectDescription(apiType: ApiType) = when (apiType) {
    ApiType.OPENAI -> stringResource(R.string.select_openai_model_description)
    ApiType.ANTHROPIC -> stringResource(R.string.select_anthropic_model_description)
    ApiType.GOOGLE -> stringResource(R.string.select_google_model_description)
    ApiType.OLLAMA -> stringResource(R.string.select_ollama_model_description)
}

/**
 * 获取动态主题标题
 *
 * @param theme 动态主题类型
 * @return 返回对应动态主题类型的标题
 */
@Composable
fun getDynamicThemeTitle(theme: DynamicTheme) = when (theme) {
    DynamicTheme.ON -> stringResource(R.string.on)
    DynamicTheme.OFF -> stringResource(R.string.off)
}

/**
 * 获取主题模式标题
 *
 * @param theme 主题模式类型
 * @return 返回对应主题模式类型的标题
 */
@Composable
fun getThemeModeTitle(theme: ThemeMode) = when (theme) {
    ThemeMode.SYSTEM -> stringResource(R.string.system_default)
    ThemeMode.DARK -> stringResource(R.string.on)
    ThemeMode.LIGHT -> stringResource(R.string.off)
}

/**
 * 获取平台设置标题
 *
 * @param apiType API类型
 * @return 返回对应API类型的平台设置标题
 */
@Composable
fun getPlatformSettingTitle(apiType: ApiType) = when (apiType) {
    ApiType.OPENAI -> stringResource(R.string.openai_setting)
    ApiType.ANTHROPIC -> stringResource(R.string.anthropic_setting)
    ApiType.GOOGLE -> stringResource(R.string.google_setting)
    ApiType.OLLAMA -> stringResource(R.string.ollama_setting)
}

/**
 * 获取平台设置描述
 *
 * @param apiType API类型
 * @return 返回对应API类型的平台设置描述
 */
@Composable
fun getPlatformSettingDescription(apiType: ApiType) = when (apiType) {
    ApiType.OPENAI -> stringResource(R.string.platform_setting_description)
    ApiType.ANTHROPIC -> stringResource(R.string.platform_setting_description)
    ApiType.GOOGLE -> stringResource(R.string.platform_setting_description)
    ApiType.OLLAMA -> stringResource(R.string.platform_setting_description)
}

/**
 * 获取平台API品牌文本
 *
 * @param apiType API类型
 * @return 返回对应API类型的品牌文本
 */
@Composable
fun getPlatformAPIBrandText(apiType: ApiType) = when (apiType) {
    ApiType.OPENAI -> stringResource(R.string.openai_brand_text)
    ApiType.ANTHROPIC -> stringResource(R.string.anthropic_brand_text)
    ApiType.GOOGLE -> stringResource(R.string.google_brand_text)
    ApiType.OLLAMA -> stringResource(R.string.ollama_brand_text)
}
