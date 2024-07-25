/*
 * Copyright (C) 2024 Abdelraouf Sabri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.abd3lraouf.product.project.builder

import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.readLines

private const val STORYTELLER_SDK_KOTLIN = "STORYTELLER_SDK_KOTLIN"
private const val SHOWCASE = "SHOWCASE"
private const val NBA = "NBA"
private const val ShowByteCode = "SHOW_BYTE_CODE"
private const val ShowDex = "SHOW_DEX"
private const val ShowOat = "SHOW_OAT"
private const val SyncLines = "SYNC_LINES"
private const val Indent = "INDENT"
private const val DecompileHiddenIsa = "DECOMPILE_HIDDEN_ISA"
private const val LineNumberWidth = "LINE_NUMBER_WIDTH"
private const val WindowPosX = "WINDOW_X"
private const val WindowPosY = "WINDOW_Y"
private const val WindowWidth = "WINDOW_WIDTH"
private const val WindowHeight = "WINDOW_HEIGHT"
private const val Placement = "WINDOW_PLACEMENT"

@Stable
class BuilderState {
    val directory: Path = settingsPath()
    private val file: Path = directory.resolve("settings")
    private val entries: MutableMap<String, String> = readSettings(file)

    var storytellerSdkKotlin by StringState(STORYTELLER_SDK_KOTLIN, "")
    var showcase by StringState(SHOWCASE, "")
    var nba by StringState(NBA, "")
    var projectPaths by mutableStateOf(createProjectPaths())
    var windowWidth by IntState(WindowWidth, 1900)
    var windowHeight by IntState(WindowHeight, 1600)
    var windowPosX by IntState(WindowPosX, -1)
    var windowPosY by IntState(WindowPosY, -1)
    var windowPlacement by SettingsState(Placement, WindowPlacement.Floating) { WindowPlacement.valueOf(this) }
    fun reloadToolPathsFromSettings() {
        projectPaths = createProjectPaths()
    }

    private fun createProjectPaths() = ProjectPaths(directory, Path.of(storytellerSdkKotlin), Path.of(showcase), Path.of(nba))

    private inner class BooleanState(key: String, initialValue: Boolean) :
        SettingsState<Boolean>(key, initialValue, { toBoolean() })

    private inner class IntState(key: String, initialValue: Int) :
        SettingsState<Int>(key, initialValue, { toInt() })

    private inner class StringState(key: String, initialValue: String) :
        SettingsState<String>(key, initialValue, { this })

    private open inner class SettingsState<T>(private val key: String, initialValue: T, parse: String.() -> T) :
        MutableState<T> {
        private val state = mutableStateOf(entries[key]?.parse() ?: initialValue)
        override var value: T
            get() = state.value
            set(value) {
                entries[key] = value.toString()
                state.value = value
            }

        override fun component1() = state.component1()

        override fun component2() = state.component2()
    }

    fun writeSourceCodeState() {
    }

    fun writeState() {
        writeSourceCodeState()
        Files.writeString(
            file,
            entries.map { (key, value) -> "$key=${value.replace("\n", "\\\n")}" }.joinToString("\n")
        )
    }
}

private fun settingsPath() = Paths.get(System.getProperty("user.home"), ".project-builder").apply {
    if (!exists()) Files.createDirectory(this)
}

private fun readSettings(file: Path): MutableMap<String, String> {
    val settings = mutableMapOf<String, String>()
    if (!file.exists()) return settings

    val lines = file.readLines()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val index = line.indexOf('=')
        if (index != -1) {
            var value = line.substring(index + 1)
            if (value.endsWith('\\')) {
                value = value.dropLast(1) + '\n'
                do {
                    i++
                    if (i >= lines.size) break
                    value += lines[i].dropLast(1) + '\n'
                } while (lines[i].endsWith('\\'))
            }
            settings[line.substring(0, index)] = value
        }
        i++
    }

    return settings
}
