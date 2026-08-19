import jdk.internal.net.http.common.Log.channel
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
    alias(libs.plugins.secrets)
}

android {
    namespace = "io.agents.pokeclaw"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "io.agents.pokeclaw"
        minSdk = 28
        targetSdk = 36
        versionCode = 29
        versionName = "0.7.1"
        buildConfigField("String", "VERSION_INFO", "\"0.7.1\"")
        buildConfigField("String", "APP_ORIGIN", "\"PokeClaw by agents.io | github.com/agents-io/PokeClaw\"")
        buildConfigField("String", "BUILD_FINGERPRINT", "\"debug\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debugConfig")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    secrets {
        propertiesFileName = ".env"
        defaultPropertiesFileName = ".env.example"
        ignoreList.add("sops_.*")
        ignoreList.add("OPENAI_API_KEY")
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.gson)


    implementation(libs.oapi.sdk)
    implementation(libs.dingtalk)


    // LangChain4j (exclude JDK http-client, use OkHttp adapter for Android)
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.openai) {
        exclude(group = "dev.langchain4j", module = "langchain4j-http-client-jdk")
    }
    implementation(libs.langchain4j.anthropic) {
        exclude(group = "dev.langchain4j", module = "langchain4j-http-client-jdk")
    }
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.ok2curl)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.mmkv)
    implementation(libs.adapter)
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    implementation(libs.easyfloat)


    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2025.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // LiteRT-LM on-device LLM inference (Google AI Edge)
    implementation("com.google.ai.edge.litertlm:litertlm-android:0.10.0")

    // ZXing 二维码/条形码扫描
    implementation(libs.zxing)

    // NanoHTTPD 嵌入式 HTTP 服务器（局域网配置服务）
    implementation(libs.nanohttpd)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("injectBuildFingerprint") {
    doLast {
        val gitHash = try {
            val p = Runtime.getRuntime().exec("git rev-parse HEAD")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            r.readLine()?.trim() ?: "unknown"
        } catch (_: Exception) { "unknown" }
        val ts = System.currentTimeMillis()
        val builder = System.getenv("BUILDER_ID") ?: System.getProperty("user.name") ?: "local"
        val fp = "t=$ts\nc=$gitHash\nb=$builder\nv=${android.defaultConfig.versionName}"
        val hexEncoded = fp.toByteArray().joinToString("") { "%02x".format(it) }
        file("src/main/assets/.pcfp").apply {
            parentFile.mkdirs()
            writeText(hexEncoded)
        }
    }
}
tasks.named("preBuild") { dependsOn("injectBuildFingerprint") }

