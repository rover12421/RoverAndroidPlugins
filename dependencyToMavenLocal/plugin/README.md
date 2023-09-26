### dependencyToMavenLocal plugin

```txt
用于gradle项目,刷新配置后,把所有项目的依赖,下载到`mavenLocal`中,
并添加`mavenLocal`到项目的`repositories`最前面.
项目下载依赖优先使用本地maven仓库,如果本地没有,则使用远程仓库.
```

##### 使用方法:

- 添加插件依赖
```kts
buildscript {
    dependencies {
        classpath("com.rover12421.gradle.plugins.dependencyToMavenLocal:plugin:8.0.1")
    }
}
```
- 任意项目使用插件
```kts
plugins {
    id("rover.gradle.dependencyToMavenLocal")
}
```

- 配置
```kts
dependencyToMavenLocal {
    // debug模式下更多的日志输出
    debug = true 
    
    // 过滤 不下载到 mavenLocal 的依赖前缀或正则 
    filter.add("com.google")
    
    // filter 的过滤规则是否为正则表达式
    filterRegex = true
    
    // 自定义repo
    repo["aliyun"] = "https://maven.aliyun.com/repository/public"
}
```

- 默认repo:

name | url
---- | ---
huaweicloud | https://mirrors.huaweicloud.com/repository/maven/
aliyun      | https://maven.aliyun.com/repository/public
aliyun gradle plugin | https://maven.aliyun.com/repository/gradle-plugin
sonatype    | https://oss.sonatype.org/service/local/repositories/releases/content/
jitpack     | https://jitpack.io

