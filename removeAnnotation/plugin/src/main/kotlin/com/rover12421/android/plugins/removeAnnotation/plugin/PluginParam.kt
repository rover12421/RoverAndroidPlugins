package com.rover12421.android.plugins.removeAnnotation.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface PluginParam : InstrumentationParameters {

    @get:Input
    val filters: ListProperty<String>

    @get:Input
    val annotations: ListProperty<String>

    @get:Input
    val debug: Property<Boolean>
}