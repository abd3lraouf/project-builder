@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask
import kotlin.io.path.listDirectoryEntries

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    id("com.dorongold.task-tree") version "2.1.1"
}

version = "1.0.0"
val baseName = "Project Builder"
val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val targetArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val target = "${targetOs}-${targetArch}"
val jdkLevel = project.property("jdk.level") as String

kotlin {
    jvm {
        @Suppress("OPT_IN_USAGE")
        mainRun {
            mainClass = "dev.abd3lraouf.product.project.builder.ProjectBuilderKt"
        }
    }

    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(jdkLevel)
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude(group = "org.jetbrains.compose.material")
                }
                implementation(libs.collection)
                implementation(libs.compose.material3)
                implementation(libs.compose.splitpane)
                implementation(libs.jewel)
                implementation(libs.jewel.decorated)
                implementation(libs.jewel.markdown)
                implementation(libs.jewel.foundation)
                implementation(libs.jna)
                implementation(libs.lifecycle)
                implementation(libs.lifecycle.compose)
                implementation(libs.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
                val skikoVersion = libs.versions.skiko.get()
                implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$skikoVersion")

                implementation(libs.kotlin.reflect)
                implementation(libs.filePicker)
                implementation(compose.desktop.currentOs) {
                    exclude(group = "org.jetbrains.compose.material")
                }
                implementation(libs.intellijPlatform.icons)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.junit4)
                implementation(libs.kotlin.test)
                implementation(libs.truth)
            }
        }
    }
}

compose.desktop {
    application {
//        mainClass = "dev.abd3lraouf.product.project.builder.ProjectBuilderKt"
        mainClass = "org.jetbrains.jewel.samples.standalone.MainKt"
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
        nativeDistributions {
            modules("jdk.unsupported")

            targetFormats(TargetFormat.Dmg)

            packageVersion = version.toString()
            packageName = baseName
            description = baseName
            vendor = "Abdelraouf Sabri"
            licenseFile = rootProject.file("LICENSE")

            macOS {
                dockName = "Project Builder"
                iconFile = file("art/icons/mac/icon.icns")
                bundleID = "dev.abd3lraouf.product.project.builder"
            }
        }
    }
}

val currentArch: String = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported OS arch: $osArch")
}

/**
 * TODO: workaround for https://github.com/JetBrains/compose-multiplatform/issues/4976.
 */
val renameDmg by tasks.registering(Copy::class) {
    group = "distribution"
    description = "Rename the DMG file"

    val packageDmg = tasks.named<AbstractJPackageTask>("packageReleaseDmg")
    // build/compose/binaries/main-release/dmg/*.dmg
    val fromFile = packageDmg.map {
        it.appImage.get().dir("../dmg").asFile.toPath()
            .listDirectoryEntries("$baseName*.dmg").single()
    }

    from(fromFile)
    into(fromFile.map { it.parent })
    rename {
        "kotlin-explorer-$currentArch-$version.dmg"
    }
}

project.afterEvaluate {
    tasks.named("packageReleaseDmg") {
        finalizedBy(renameDmg)
    }
}

tasks {
    withType<JavaExec> {
        // afterEvaluate is needed because the Compose Gradle Plugin
        // register the task in the afterEvaluate block
        afterEvaluate {
            javaLauncher =
                project.javaToolchains.launcherFor {
                    languageVersion = JavaLanguageVersion.of(jdkLevel)
                }
            setExecutable(javaLauncher.map { it.executablePath.asFile.absolutePath }.get())
        }
    }
}
