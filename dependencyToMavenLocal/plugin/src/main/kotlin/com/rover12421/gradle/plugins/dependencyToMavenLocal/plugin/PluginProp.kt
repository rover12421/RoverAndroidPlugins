package com.rover12421.gradle.plugins.dependencyToMavenLocal.plugin

open class PluginProp {
    var debug: Boolean = false

    /**
     * 过滤 依赖过滤 规则列表
     * 默认是(filterRegex = false) startsWith
     * filterRegex = ture 是正则模式
     */
    var filter: MutableList<String> = mutableListOf()

    /**
     * filter 列表是否是正则
     * 默认false,使用archive全名前缀匹配
     */
    var filterRegex: Boolean = false

    /**
     * 仓库地址
     * key: 仓库名
     * value: 仓库地址
     */
    var repo: MutableMap<String, String> = mutableMapOf()

}
