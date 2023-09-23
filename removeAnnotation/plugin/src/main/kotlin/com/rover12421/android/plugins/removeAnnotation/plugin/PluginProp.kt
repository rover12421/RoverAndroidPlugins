package com.rover12421.android.plugins.removeAnnotation.plugin

open class PluginProp {
    var debug: Boolean = false

    /**
     * 是否处理所有项目和依赖class
     * 默认false, 只处理当前项目
     */
    var allProject: Boolean = false

    /**
     * 过滤class规则列表
     */
    var filter: MutableList<String> = mutableListOf()

    /**
     * filter 列表是否是正则
     * 默认false,使用class全名前缀匹配
     */
    var filterRegex: Boolean = false

    /**
     * 需要移除的注解列表
     */
    var annotations: MutableList<String> = mutableListOf()
}