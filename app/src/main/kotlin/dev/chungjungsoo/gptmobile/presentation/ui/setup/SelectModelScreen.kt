package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.ModelConstants.anthropicModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.googleModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModelDescriptions
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.openaiModels
import dev.chungjungsoo.gptmobile.data.dto.APIModel
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.presentation.common.PrimaryLongButton
import dev.chungjungsoo.gptmobile.presentation.common.RadioItem
import dev.chungjungsoo.gptmobile.util.*

/**
 * 选择模型屏幕
 *
 * @param modifier 修饰符
 * @param setupViewModel 设置视图模型
 * @param currentRoute 当前路由
 * @param platformType API类型
 * @param onNavigate 导航回调
 * @param onBackAction 返回操作回调
 */
@Composable
fun SelectModelScreen(
    modifier: Modifier = Modifier,
    setupViewModel: SetupViewModel = hiltViewModel(),
    currentRoute: String,
    platformType: ApiType,
    onNavigate: (route: String) -> Unit,
    onBackAction: () -> Unit
) {
    // 获取标题和描述
    val title = getAPIModelSelectTitle(platformType)
    val description = getAPIModelSelectDescription(platformType)

    // 根据平台类型获取可用模型列表
    val availableModels = when (platformType) {
        ApiType.OPENAI -> generateOpenAIModelList(models = openaiModels)
        ApiType.ANTHROPIC -> generateAnthropicModelList(models = anthropicModels)
        ApiType.GOOGLE -> generateGoogleModelList(models = googleModels)
        ApiType.OLLAMA -> generateOllamaModelList(models = ollamaModels, descriptions = ollamaModelDescriptions)
    }

    // 设置默认模型
    val defaultModel = remember {
        derivedStateOf {
            setupViewModel.setDefaultModel(
                platformType,
                when (platformType) {
                    ApiType.OPENAI -> 0
                    ApiType.ANTHROPIC -> 0
                    ApiType.GOOGLE -> 1
                    ApiType.OLLAMA -> 0
                }
            )
        }
    }

    // 获取平台状态
    val platformState by setupViewModel.platformState.collectManagedState()
    val model = platformState.firstOrNull { it.name == platformType }?.model ?: defaultModel.value

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SetupAppBar(onBackAction) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 显示选择模型的文本
            SelectModelText(title = title, description = description)

            // 显示模型选择列表
            ModelRadioGroup(
                availableModels = availableModels,
                model = model,
                onChangeEvent = { model -> setupViewModel.updateModel(platformType, model) }
            )
            Spacer(modifier = Modifier.weight(1f))

            // 下一步按钮
            PrimaryLongButton(
                enabled = availableModels.any { it.aliasValue == model },
                onClick = {
                    val nextStep = setupViewModel.getNextSetupRoute(currentRoute)
                    onNavigate(nextStep)
                },
                text = stringResource(R.string.next)
            )
        }
    }
}

/**
 * 选择模型文本组件
 *
 * @param modifier 修饰符
 * @param title 标题
 * @param description 描述
 */
@Composable
fun SelectModelText(
    modifier: Modifier = Modifier,
    title: String,
    description: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // 显示标题
        Text(
            modifier = Modifier
                .padding(4.dp)
                .semantics { heading() },
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        // 显示描述
        Text(
            modifier = Modifier.padding(4.dp),
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 模型单选组件
 *
 * @param modifier 修饰符
 * @param availableModels 可用模型列表
 * @param model 当前选中的模型
 * @param onChangeEvent 模型选择变更回调
 */
@Composable
fun ModelRadioGroup(
    modifier: Modifier = Modifier,
    availableModels: List<APIModel>,
    model: String,
    onChangeEvent: (String) -> Unit
) {
    Column(modifier = modifier) {
        // 遍历可用模型并显示单选项
        availableModels.forEach { m ->
            RadioItem(
                value = m.aliasValue,
                selected = model == m.aliasValue,
                title = m.name,
                description = m.description,
                onSelected = onChangeEvent
            )
        }
    }
}
