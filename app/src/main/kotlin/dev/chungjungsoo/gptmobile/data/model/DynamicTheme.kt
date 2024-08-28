package dev.chungjungsoo.gptmobile.data.model

/**
 * 动态主题枚举类
 *
 * 此枚举类定义了动态主题的开启和关闭状态。
 * 它提供了一种方便的方式来表示和管理应用程序的动态主题设置。
 */
enum class DynamicTheme {
    /** 表示动态主题开启状态 */
    ON,
    /** 表示动态主题关闭状态 */
    OFF;

    companion object {
        /**
         * 根据给定的整数值获取对应的DynamicTheme枚举实例
         *
         * @param value 整数值，对应枚举的序数
         * @return 返回对应的DynamicTheme枚举实例，如果没有找到则返回null
         */
        fun getByValue(value: Int) = entries.firstOrNull { it.ordinal == value }
    }
}
