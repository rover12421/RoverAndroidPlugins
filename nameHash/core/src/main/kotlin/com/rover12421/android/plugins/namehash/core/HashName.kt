package com.rover12421.android.plugins.namehash.core

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HashName(
    vararg val names: String
)
