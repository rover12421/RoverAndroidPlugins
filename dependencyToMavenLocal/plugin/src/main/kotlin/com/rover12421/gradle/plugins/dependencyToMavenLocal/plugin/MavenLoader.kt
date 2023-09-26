package com.rover12421.gradle.plugins.dependencyToMavenLocal.plugin

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact
import org.jboss.shrinkwrap.resolver.impl.maven.ConfigurableMavenResolverSystemImpl
import org.jboss.shrinkwrap.resolver.spi.loader.ServiceRegistry
import org.jboss.shrinkwrap.resolver.spi.loader.SpiServiceLoader


class MavenLoader {
//    private val repo = Maven.configureResolver()
    private val repo = ConfigurableMavenResolverSystemImpl().apply {
        val spiServiceLoader = SpiServiceLoader()
        val serviceRegistry = ServiceRegistry(spiServiceLoader)
        ServiceRegistry.register(serviceRegistry)
    }
        .withMavenCentralRepo(false)
        .withRemoteRepo("huaweicloud", "https://mirrors.huaweicloud.com/repository/maven/", "default")
        .withRemoteRepo("aliyun", "https://maven.aliyun.com/repository/public", "default")
        .withRemoteRepo("aliyun_gradle_plugin", "https://maven.aliyun.com/repository/gradle-plugin", "default")
        .withRemoteRepo("sonatype", "https://oss.sonatype.org/service/local/repositories/releases/content/", "default")
        .withRemoteRepo("jitpack", "https://jitpack.io", "default")

    /**
     * 添加仓库
     * 默认已经包含
     * 华为云                  https://mirrors.huaweicloud.com/repository/maven/
     * 阿里云public            https://maven.aliyun.com/repository/public
     * 阿里云gradle-plugin     https://maven.aliyun.com/repository/gradle-plugin
     * sonatype               https://oss.sonatype.org/service/local/repositories/releases/content/
     * jitpack                https://jitpack.io
     */
    fun addRepo(name: String, url: String) = apply { repo.withRemoteRepo(name, url, "default") }

    val deps = mutableListOf<String>()

    /**
     * 添加依赖
     * 例如:
     * junit:junit:4.12
     * org.apache.commons:commons-lang3:3.5
     */
    fun addDependencies(vararg dependencies: String) = apply { dependencies.forEach { deps.add(it) } }

    /**
     * 获取依赖所有文件列表
     */
    fun resolveDependency(): Array<MavenResolvedArtifact> {
        if (deps.isEmpty()) {
            return emptyArray()
        }
        return try {
            repo.resolve(*deps.toTypedArray())
                .withTransitivity()
                .asResolvedArtifact()
        } catch (e: Throwable) {
            if (!e.localizedMessage.contains("No dependencies were set for resolution")) {
                println("[getDependencyFiles] $e")
            } else {
                println(e.localizedMessage)
            }
             emptyArray()
        }
    }
}

fun main() {
    MavenLoader()
        .addDependencies("org.apache.commons:commons-lang3:3.9", "org.apache.commons:commons-text:1.6")
        .resolveDependency().forEach {
            println("${it.coordinate.toCanonicalForm()} > ${it.asFile()}")
        }
}