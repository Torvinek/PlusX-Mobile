import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

val versionProperties = Properties().apply {
    val file = rootProject.file("version.properties")
    if (!file.exists()) error("version.properties is missing.")
    file.inputStream().use(::load)
}

fun projectSetting(name: String, defaultValue: String = ""): String =
    providers.gradleProperty(name).orNull
        ?: System.getenv(name)
        ?: localProperties.getProperty(name)
        ?: defaultValue

fun quoted(value: String): String =
    "\"${value.trim().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "")}\""

val signingProperties = Properties().apply {
    val file = rootProject.file("signing.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun requiredSigningProperty(name: String): String =
    signingProperties.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: error(
            """
            OFFICIAL SIGNING KEY NOT CONFIGURED.

            Missing signing.properties value: $name
            Configure signing.properties or GitHub Actions secrets.
            Do not generate or use a different signing key.
            """.trimIndent()
        )

val hasOfficialSigning = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
    .all { !signingProperties.getProperty(it).isNullOrBlank() }

android {
    namespace = "pl.torvinek.plusxmobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.torvinek.plusxmobile"
        minSdk = 28
        targetSdk = 35
        versionCode = versionProperties.getProperty("VERSION_CODE").toInt()
        versionName = versionProperties.getProperty("VERSION_NAME")

        buildConfigField(
            "String",
            "PORTAL_BASE_URL",
            quoted(projectSetting("PLUSX_PORTAL_BASE_URL", "https://new.plusx.tv"))
        )
        buildConfigField(
            "String",
            "BACKEND_BASE_URL",
            quoted(projectSetting("PLUSX_BACKEND_BASE_URL", "https://backend.torvinek.pl"))
        )
        buildConfigField(
            "String",
            "BACKEND_TOKEN",
            quoted(projectSetting("PLUSX_BACKEND_TOKEN"))
        )
        buildConfigField(
            "String",
            "FIREBASE_APPLICATION_ID",
            quoted(projectSetting("PLUSX_FIREBASE_APPLICATION_ID"))
        )
        buildConfigField(
            "String",
            "FIREBASE_API_KEY",
            quoted(projectSetting("PLUSX_FIREBASE_API_KEY"))
        )
        buildConfigField(
            "String",
            "FIREBASE_PROJECT_ID",
            quoted(projectSetting("PLUSX_FIREBASE_PROJECT_ID"))
        )
        buildConfigField(
            "String",
            "FIREBASE_GCM_SENDER_ID",
            quoted(projectSetting("PLUSX_FIREBASE_GCM_SENDER_ID"))
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    if (hasOfficialSigning) {
        signingConfigs {
            create("official") {
                storeFile = rootProject.file(requiredSigningProperty("storeFile"))
                storePassword = requiredSigningProperty("storePassword")
                keyAlias = requiredSigningProperty("keyAlias")
                keyPassword = requiredSigningProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            if (hasOfficialSigning) {
                signingConfig = signingConfigs.getByName("official")
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            if (hasOfficialSigning) {
                signingConfig = signingConfigs.getByName("official")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging")
}

gradle.taskGraph.whenReady {
    val apkTasks = gradle.startParameter.taskNames.filter {
        it.contains("assemble", ignoreCase = true) ||
            it.contains("bundle", ignoreCase = true) ||
            it.contains("install", ignoreCase = true) ||
            it.contains("package", ignoreCase = true)
    }
    if (apkTasks.isNotEmpty() && !hasOfficialSigning) {
        error(
            """
            OFFICIAL SIGNING KEY NOT CONFIGURED.

            Configure signing.properties or GitHub Actions secrets.
            Do not generate or use a different signing key.
            """.trimIndent()
        )
    }
}
