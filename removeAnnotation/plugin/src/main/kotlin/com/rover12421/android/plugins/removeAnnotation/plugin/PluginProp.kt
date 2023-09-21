package com.rover12421.android.plugins.removeAnnotation.plugin

open class PluginProp {
    var debug: Boolean = false
    var allProject: Boolean = false
    var filter: MutableList<String> = mutableListOf()
    var annotations: MutableList<String> = mutableListOf()
}