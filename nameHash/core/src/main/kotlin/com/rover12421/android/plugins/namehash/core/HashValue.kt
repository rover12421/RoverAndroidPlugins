package com.rover12421.android.plugins.namehash.core

/**
 * Created by rover12421 on 6/4/21.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HashValue(
    val len: Int,
    val hashcode: Int,
    val hash: Long,
)
