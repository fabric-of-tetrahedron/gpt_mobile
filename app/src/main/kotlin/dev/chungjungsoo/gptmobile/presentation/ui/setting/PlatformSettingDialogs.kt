package dev.chungjungsoo.gptmobile.presentation.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.ModelConstants.anthropicModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.getDefaultAPIUrl
import dev.chungjungsoo.gptmobile.data.ModelConstants.googleModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModelDescriptions
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.openaiModels
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import dev.chungjungsoo.gptmobile.presentation.common.RadioItem
import dev.chungjungsoo.gptmobile.presentation.common.TokenInputField
import dev.chungjungsoo.gptmobile.util.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.math.roundToInt
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.jsoup.Jsoup

/**
 * API URL设置对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param initialValue 初始URL值
 * @param settingViewModel 设置视图模型
 */
@Composable
fun APIUrlDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    initialValue: String,
    settingViewModel: SettingViewModel
) {
    // 如果API URL对话框打开，则显示对话框
    if (dialogState.isApiUrlDialogOpen) {
        APIUrlDialog(
            apiType = apiType,
            initialValue = initialValue,
            onDismissRequest = settingViewModel::closeApiUrlDialog,
            onResetRequest = {
                // 重置URL为默认值
                settingViewModel.updateURL(apiType, getDefaultAPIUrl(apiType))
                settingViewModel.savePlatformSettings()
                settingViewModel.closeApiUrlDialog()
            },
            onConfirmRequest = { apiUrl ->
                // 更新URL并保存设置
                settingViewModel.updateURL(apiType, apiUrl)
                settingViewModel.savePlatformSettings()
                settingViewModel.closeApiUrlDialog()
            }
        )
    }
}

/**
 * API密钥设置对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param settingViewModel 设置视图模型
 */
@Composable
fun APIKeyDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    settingViewModel: SettingViewModel
) {
    // 如果API密钥对话框打开，则显示对话框
    if (dialogState.isApiTokenDialogOpen) {
        APIKeyDialog(
            apiType = apiType,
            onDismissRequest = settingViewModel::closeApiTokenDialog
        ) { apiToken ->
            // 更新API密钥并保存设置
            settingViewModel.updateToken(apiType, apiToken)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeApiTokenDialog()
        }
    }
}

/**
 * 模型选择对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param model 当前选择的模型
 * @param settingViewModel 设置视图模型
 */
@Composable
fun ModelDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    model: String?,
    settingViewModel: SettingViewModel
) {
    // 如果模型选择对话框打开，则显示对话框
    if (dialogState.isApiModelDialogOpen) {
        ModelDialog(
            apiType = apiType,
            model = model ?: "",
            onModelSelected = { m -> settingViewModel.updateModel(apiType, m) },
            onDismissRequest = settingViewModel::closeApiModelDialog,
            settingViewModel.settingRepository
        ) { m ->
            // 更新选择的模型并保存设置
            settingViewModel.updateModel(apiType, m)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeApiModelDialog()
        }
    }
}

/**
 * 温度设置对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param temperature 当前温度值
 * @param settingViewModel 设置视图模型
 */
@Composable
fun TemperatureDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    temperature: Float,
    settingViewModel: SettingViewModel
) {
    // 如果温度设置对话框打开，则显示对话框
    if (dialogState.isTemperatureDialogOpen) {
        TemperatureDialog(
            apiType = apiType,
            temperature = temperature,
            onDismissRequest = settingViewModel::closeTemperatureDialog
        ) { temp ->
            // 更新温度值并保存设置
            settingViewModel.updateTemperature(apiType, temp)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeTemperatureDialog()
        }
    }
}

/**
 * Top P设置对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param topP 当前Top P值
 * @param settingViewModel 设置视图模型
 */
@Composable
fun TopPDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    topP: Float?,
    settingViewModel: SettingViewModel
) {
    // 如果Top P设置对话框打开，则显示对话框
    if (dialogState.isTopPDialogOpen) {
        TopPDialog(
            topP = topP,
            onDismissRequest = settingViewModel::closeTopPDialog
        ) { p ->
            // 更新Top P值并保存设置
            settingViewModel.updateTopP(apiType, p)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeTopPDialog()
        }
    }
}

/**
 * 系统提示词设置对话框
 *
 * @param dialogState 对话框状态
 * @param apiType API类型
 * @param systemPrompt 当前系统提示词
 * @param settingViewModel 设置视图模型
 */
@Composable
fun SystemPromptDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    systemPrompt: String,
    settingViewModel: SettingViewModel
) {
    // 如果系统提示词对话框打开，则显示对话框
    if (dialogState.isSystemPromptDialogOpen) {
        SystemPromptDialog(
            prompt = systemPrompt,
            onDismissRequest = settingViewModel::closeSystemPromptDialog
        ) {
            // 更新系统提示词并保存设置
            settingViewModel.updateSystemPrompt(apiType, it)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeSystemPromptDialog()
        }
    }
}

/**
 * API URL设置对话框的具体实现
 *
 * @param apiType API类型
 * @param initialValue 初始URL值
 * @param onDismissRequest 关闭对话框的回调
 * @param onResetRequest 重置URL的回调
 * @param onConfirmRequest 确认URL的回调
 */
@Composable
private fun APIUrlDialog(
    apiType: ApiType,
    initialValue: String,
    onDismissRequest: () -> Unit,
    onResetRequest: () -> Unit,
    onConfirmRequest: (url: String) -> Unit
) {
    // 使用remember存储API URL的状态
    var apiUrl by remember { mutableStateOf(initialValue) }
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.api_url)) },
        text = {
            // API URL输入框
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                value = apiUrl,
                isError = apiUrl.isValidUrl().not(),
                onValueChange = { apiUrl = it },
                label = {
                    Text(stringResource(R.string.api_url))
                },
                supportingText = {
                    if (apiUrl.isValidUrl().not()) {
                        Text(text = stringResource(R.string.invalid_api_url))
                    }
                }
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // 确认按钮
            TextButton(
                enabled = apiUrl.isNotBlank() && apiUrl.isValidUrl(),
                onClick = { onConfirmRequest(apiUrl) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Row {
                // 重置按钮
                TextButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = onResetRequest
                ) {
                    Text(stringResource(R.string.reset))
                }
                // 取消按钮
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}

/**
 * API密钥对话框组件
 *
 * @param apiType API类型
 * @param onDismissRequest 关闭对话框的回调函数
 * @param onConfirmRequest 确认API密钥的回调函数
 */
@Composable
private fun APIKeyDialog(
    apiType: ApiType,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (token: String) -> Unit
) {
    // 使用remember存储token状态
    var token by remember { mutableStateOf("") }
    // 获取当前配置
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = getPlatformAPILabelResources()[apiType]!!) },
        text = {
            // 自定义令牌输入字段
            TokenInputField(
                value = token,
                onValueChange = { token = it },
                onClearClick = { token = "" },
                label = getPlatformAPILabelResources()[apiType]!!,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                helpLink = getPlatformHelpLinkResources()[apiType]!!
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // 确认按钮，只有在token不为空时才能点击
            TextButton(
                enabled = token.isNotBlank(),
                onClick = { onConfirmRequest(token) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            // 取消按钮
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 模型选择对话框组件
 *
 * @param apiType API类型
 * @param model 当前选中的模型
 * @param onModelSelected 模型选择回调函数
 * @param onDismissRequest 关闭对话框的回调函数
 * @param settingRepository 设置仓库
 * @param onConfirmRequest 确认模型选择的回调函数
 */
@Composable
private fun ModelDialog(
    apiType: ApiType,
    model: String,
    onModelSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    settingRepository: SettingRepository,
    onConfirmRequest: (model: String) -> Unit
) {
    // 存储新的Ollama模型列表
    val ollamaModelsNew = linkedSetOf<String>()

    runBlocking {
        // 获取Ollama平台信息
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OLLAMA })

        // 发送HTTP请求获取模型列表
        val response = HttpClient().use { client ->
            client.get("${platform.apiUrl}/api/tags")
        }
        val models = response.bodyAsText().let { body ->
            val jsonObject = JSONObject(body)
            jsonObject.getJSONArray("models")
        }
        // 解析模型名称并添加到列表中
        for (i in 0 until models.length()) {
            val model = models.getJSONObject(i)
            val modelName = model.getString("name")
            val modelName_02 = modelName.substringBefore(":")
            ollamaModelsNew.add(modelName_02)
        }
        // 更新全局Ollama模型列表
        ollamaModels.clear()
        ollamaModels.addAll(ollamaModelsNew)

        // 获取Ollama模型描述
        val ollamaModelDescriptionsNew = mutableMapOf<String, String>()
        val client = HttpClient()
        for (modelName in ollamaModelsNew) {
            val url = "https://ollama.com/library/$modelName"
            val descriptionResponse = client.get(url)
            val document = Jsoup.parse(descriptionResponse.bodyAsText())
            val descriptionElement = document.selectXpath("/html/body/div/main/section[1]/h2").firstOrNull()
            val description = descriptionElement?.text()
            if (description != null) ollamaModelDescriptionsNew[modelName] = description
        }
        client.close()
        // 更新全局Ollama模型描述
        ollamaModelDescriptions.putAll(ollamaModelDescriptionsNew)
    }

    // 根据API类型选择对应的模型列表
    val modelList = when (apiType) {
        ApiType.OPENAI -> openaiModels
        ApiType.ANTHROPIC -> anthropicModels
        ApiType.GOOGLE -> googleModels
        ApiType.OLLAMA -> ollamaModels
    }
    // 生成可用模型列表
    val availableModels = when (apiType) {
        ApiType.OPENAI -> generateOpenAIModelList(models = modelList)
        ApiType.ANTHROPIC -> generateAnthropicModelList(models = modelList)
        ApiType.GOOGLE -> generateGoogleModelList(models = modelList)
        ApiType.OLLAMA -> generateOllamaModelList(models = modelList, descriptions = ollamaModelDescriptions)
    }
    // 获取当前配置
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.api_model)) },
        text = {
            // 显示可选模型列表
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                availableModels.forEach { m ->
                    RadioItem(
                        value = m.aliasValue,
                        selected = model == m.aliasValue,
                        title = m.name,
                        description = m.description,
                        onSelected = { onModelSelected(it) }
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // 确认按钮，只有在选择了有效模型时才能点击
            TextButton(
                enabled = model.isNotBlank() && model in modelList,
                onClick = { onConfirmRequest(model) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            // 取消按钮
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 温度设置对话框
 *
 * @param apiType API类型，用于确定温度范围
 * @param temperature 当前温度值
 * @param onDismissRequest 对话框关闭请求的回调
 * @param onConfirmRequest 确认温度设置的回调
 */
@Composable
private fun TemperatureDialog(
    apiType: ApiType,
    temperature: Float,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (temp: Float) -> Unit
) {
    // 获取当前配置
    val configuration = LocalConfiguration.current
    // 用于文本输入的温度值
    var textFieldTemperature by remember { mutableStateOf(temperature.toString()) }
    // 用于滑块的温度值
    var sliderTemperature by remember { mutableFloatStateOf(temperature) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.temperature_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 温度设置说明
                Text(stringResource(R.string.temperature_setting_description))
                // 温度输入框
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldTemperature,
                    onValueChange = { t ->
                        textFieldTemperature = t
                        val converted = t.toFloatOrNull()
                        converted?.let {
                            // 根据API类型限制温度范围
                            sliderTemperature = when (apiType) {
                                ApiType.ANTHROPIC -> it.coerceIn(0F, 1F)
                                else -> it.coerceIn(0F, 2F)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(stringResource(R.string.temperature))
                    }
                )
                // 温度滑块
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = sliderTemperature,
                    // 根据API类型设置滑块范围
                    valueRange = when (apiType) {
                        ApiType.ANTHROPIC -> 0F..1F
                        else -> 0F..2F
                    },
                    // 根据API类型设置滑块步数
                    steps = when (apiType) {
                        ApiType.ANTHROPIC -> 10 - 1
                        else -> 20 - 1
                    },
                    onValueChange = { t ->
                        sliderTemperature = t
                        textFieldTemperature = t.toString()
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // 确认按钮
            TextButton(
                onClick = { onConfirmRequest(sliderTemperature) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            // 取消按钮
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 顶部P值设置对话框
 *
 * @param topP 当前的顶部P值
 * @param onDismissRequest 对话框关闭请求的回调
 * @param onConfirmRequest 确认请求的回调，参数为新的顶部P值
 */
@Composable
private fun TopPDialog(
    topP: Float?,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (topP: Float) -> Unit
) {
    val configuration = LocalConfiguration.current
    // 初始化文本字段的顶部P值
    var textFieldTopP by remember { mutableStateOf((topP ?: 1F).toString()) }
    // 初始化滑块的顶部P值
    var sliderTopP by remember { mutableFloatStateOf(topP ?: 1F) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.top_p_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 显示顶部P值设置的描述
                Text(stringResource(R.string.top_p_setting_description))
                // 顶部P值输入字段
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldTopP,
                    onValueChange = { p ->
                        textFieldTopP = p
                        // 将输入值转换为浮点数并限制在0.1到1之间
                        p.toFloatOrNull()?.let {
                            val rounded = (it.coerceIn(0.1F, 1F) * 100).roundToInt() / 100F
                            sliderTopP = rounded
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(stringResource(R.string.top_p))
                    }
                )
                // 顶部P值滑块
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = sliderTopP,
                    valueRange = 0.1F..1F,
                    steps = 8,
                    onValueChange = { t ->
                        // 四舍五入到小数点后两位
                        val rounded = (t * 100).roundToInt() / 100F
                        sliderTopP = rounded
                        textFieldTopP = rounded.toString()
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onConfirmRequest(sliderTopP) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 系统提示对话框
 *
 * @param prompt 当前的系统提示文本
 * @param onDismissRequest 对话框关闭请求的回调
 * @param onConfirmRequest 确认请求的回调，参数为新的系统提示文本
 */
@Composable
private fun SystemPromptDialog(
    prompt: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (text: String) -> Unit
) {
    val configuration = LocalConfiguration.current
    // 初始化系统提示文本字段
    var textFieldPrompt by remember { mutableStateOf(prompt) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.system_prompt_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 显示系统提示设置的描述
                Text(stringResource(R.string.system_prompt_description))
                // 系统提示输入字段
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldPrompt,
                    onValueChange = { textFieldPrompt = it },
                    label = {
                        Text(stringResource(R.string.system_prompt))
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                // 只有当系统提示不为空时才启用确认按钮
                enabled = textFieldPrompt.isNotBlank(),
                onClick = { onConfirmRequest(textFieldPrompt) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
