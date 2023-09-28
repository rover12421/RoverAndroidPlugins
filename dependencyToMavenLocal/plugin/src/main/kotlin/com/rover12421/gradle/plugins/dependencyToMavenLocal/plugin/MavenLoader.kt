package com.rover12421.gradle.plugins.dependencyToMavenLocal.plugin

import org.apache.maven.model.Model
import org.apache.maven.repository.internal.ArtifactDescriptorReaderDelegate
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyFilter
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactDescriptorResult
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.supplier.RepositorySystemSupplier
import java.io.File


class MavenLoader {
    val collectRequest = CollectRequest()
    val session = MavenRepositorySystemUtils.newSession()
    val system = RepositorySystemSupplier().get()
    val dependencyFilter = DependencyFilter { _, _ ->
        true
    }

    /**
     * org.gradle.internal.component.external.model.maven.DefaultMavenModuleResolveMetadata.JAR_PACKAGINGS
     */
    val JAR_PACKAGINGS: Set<String> = setOf("", "pom", "jar", "ejb", "bundle", "maven-plugin", "eclipse-plugin")

    init {
        /**
         * 添加仓库
         * 默认已经包含
         * 华为云                  https://mirrors.huaweicloud.com/repository/maven/
         * 阿里云public            https://maven.aliyun.com/repository/public
         * 阿里云gradle-plugin     https://maven.aliyun.com/repository/gradle-plugin
         * sonatype               https://oss.sonatype.org/service/local/repositories/releases/content/
         * jitpack                https://jitpack.io
         */
        val localRepoUrl = System.getProperty("maven.repo.local", System.getProperty("user.home") + "/.m2/repository")
        val localRepository = LocalRepository(localRepoUrl)
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepository)

        addRepo("mavenLocal", File(localRepoUrl).toURI().toURL().toString())
        addRepo("huaweiCloud", "https://mirrors.huaweicloud.com/repository/maven/")
        addRepo("aliyun", "https://maven.aliyun.com/repository/public")
        addRepo("aliyun gradle plugin", "https://maven.aliyun.com/repository/gradle-plugin")
        addRepo("sonatype", "https://oss.sonatype.org/service/local/repositories/releases/content/")
        addRepo("jitpack", "https://jitpack.io")


        /**
         * 默认的版本适配会遇到异常
         * 置null,会下载所有遇到的版本
         */
        session.dependencyGraphTransformer = null

        val artifactDescriptorReaderDelegate = object : ArtifactDescriptorReaderDelegate() {
            override fun populateResult(
                session: RepositorySystemSession,
                result: ArtifactDescriptorResult,
                model: Model
            ) {
                val resultArtifact= result.artifact
                val packaging = model.packaging
                var classifier = resultArtifact.classifier
                var extension = resultArtifact.extension

                if (packaging != extension) {
                    extension = if (packaging in JAR_PACKAGINGS) {
                        "jar"
                    } else {
                        packaging
                    }

                    if (packaging == "pom") {
                        if (model.dependencyManagement == null) {
                            classifier = "all"
                        } else {
                            extension = "pom"
                            model.dependencies.addAll(model.dependencyManagement.dependencies)
                        }
                    }

                    result.artifact = DefaultArtifact(
                        /* groupId = */ resultArtifact.groupId,
                        /* artifactId = */ resultArtifact.artifactId,
                        /* classifier = */ classifier,
                        /* extension = */ extension,
                        /* version = */ resultArtifact.version
                    )

                    if (PluginMain.prop.debug) {
                        println("new result artifact: ${result.artifact}")
                    }
                }
                super.populateResult(session, result, model)
            }
        }
        session.setConfigProperty(ArtifactDescriptorReaderDelegate::class.java.name, artifactDescriptorReaderDelegate)
    }

    fun addRepo(name: String, url: String, type: String = "default") = apply {
        collectRequest.addRepository(
            RemoteRepository.Builder(name, type,  url)
                .build())
    }

    /**
     * 添加依赖
     * 例如:
     * junit:junit:4.12
     * org.apache.commons:commons-lang3:3.5
     */
    fun addDependency(dependency: String) = apply {
        collectRequest.addDependency(Dependency(DefaultArtifact(dependency), null))
    }

    fun addDependencies(vararg dependencies: String) = apply { dependencies.forEach { addDependency(it) } }

    /**
     * 获取依赖所有文件列表
     */
    fun resolveDependency(): List<Artifact> {
        try {
            val defRequest = DependencyRequest(collectRequest, dependencyFilter)
            val dependencyResult = system.resolveDependencies(session, defRequest)
            return dependencyResult.artifactResults.map { it.artifact }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return emptyList()
    }
}

fun main() {
    MavenLoader()
        .addDependencies(
//            "org.gradle.toolchains:foojay-resolver:0.7.0",
//            "org.jdeferred:jdeferred-android-aar:1.2.6",
//            "com.tencent.bugly:nativecrashreport:aar:3.9.0",
//            "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:3.1.3"

            "com.google.prefab:cli:2.0.0"
        )
        .resolveDependency().forEach {
            println("[$it] > ${it.file}")
        }
}