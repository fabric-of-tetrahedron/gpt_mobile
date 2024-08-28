package dev.chungjungsoo.gptmobile.data.dto

/**
 * ApiState 密封类
 *
 * 这个密封类用于表示API调用的不同状态。
 * 它包含四个内部类/对象，分别代表加载中、成功、错误和完成状态。
 */
sealed class ApiState {
    /**
     * 表示API调用正在加载中的状态
     */
    data object Loading : ApiState()

    /**
     * 表示API调用成功的状态
     *
     * @property textChunk 成功返回的文本块
     */
    data class Success(val textChunk: String) : ApiState()

    /**
     * 表示API调用出错的状态
     *
     * @property message 错误信息
     */
    data class Error(val message: String) : ApiState()

    /**
     * 表示API调用完成的状态
     */
    data object Done : ApiState()
}
