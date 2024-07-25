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

@file:Suppress("FunctionName")

package dev.abd3lraouf.product.project.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.Key.Companion.S
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowPosition.Aligned
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import dev.abd3lraouf.product.project.builder.Shortcut.Ctrl
import dev.abd3lraouf.product.project.builder.Shortcut.CtrlAlt
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle
import java.awt.Desktop
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

private class UiState(val builderState: BuilderState, window: ComposeWindow) {
    var status by mutableStateOf("Ready")
    var progress by mutableStateOf(1f)
    var logs by mutableStateOf(AnnotatedString(""))

    var showSettings by mutableStateOf(!builderState.projectPaths.isValid)

    val onProgressUpdate: (String, Float) -> Unit = { newStatus: String, newProgress: Float ->
        if (newStatus.isNotEmpty()) {
            status = newStatus
        }
        progress = newProgress
    }

    val onLogsUpdate: (AnnotatedString) -> Unit = { text ->
        logs = text
        if (text.isNotEmpty()) {

        }
    }
}

@Composable
private fun FrameWindowScope.projectBuilder(
    builderState: BuilderState
) {
    val uiState = remember { UiState(builderState, window) }

    MainMenu(
        builderState
    )

    if (isMac) {
        Desktop.getDesktop().setPreferencesHandler {

        }
    }

    if (uiState.showSettings) {
//        SettingsDialog(builderState, uiState)
    }

}

@Composable
private fun SettingsDialog(
    builderState: BuilderState, uiState: UiState
) {
    val onDismissRequest = {

    }
    val onSaveRequest = {

    }

    DialogWindow(onCloseRequest = onDismissRequest,
        state = rememberDialogState(size = DpSize(592.dp, 385.dp)),
        visible = true,
        title = "Settings",
        resizable = true,
        onPreviewKeyEvent = { keyEvent ->
            if (keyEvent.key == Key.Escape) {
                onDismissRequest()
                true
            } else {
                false
            }
        }) {
        Settings(builderState, onSaveRequest = onSaveRequest, onDismissRequest = onDismissRequest)
    }
}

@Composable
private fun LogsPanel(logs: AnnotatedString) {
    Column {
        Title("Logs")
        SelectionContainer {
            Text(
                text = logs,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxSize().background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .border(1.dp, JewelTheme.globalColors.borders.normal).padding(8.dp)
                    .focusable(false)
            )
        }
    }
}

@Composable
private fun StatusBar(status: String, progress: Float) {
    Row(verticalAlignment = CenterVertically) {
        val width = 220.dp
        Text(
            modifier = Modifier.widthIn(min = width, max = width).padding(8.dp), text = status
        )
        if (progress < 1) {
            LinearProgressIndicator(
                progress = { progress },
                color = ProgressColor,
                trackColor = ProgressTrackColor,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

private fun BuilderState.getPanels(
    sourcePanel: @Composable () -> Unit,
    byteCodePanel: @Composable () -> Unit,
    dexPanel: @Composable () -> Unit,
    oatPanel: @Composable () -> Unit,
): List<@Composable () -> Unit> {
    return buildList {
    }
}



@Composable
private fun Title(text: String) {
    Text(
        text, textAlign = Center, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}


@Composable
private fun FrameWindowScope.MainMenu(
    builderState: BuilderState,
    onStatusUpdate: (String, Float) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()


    MenuBar {
        Menu("File") {
            MenuItem("Save", Ctrl(S), onClick = builderState::writeSourceCodeState)
            if (!isMac) {
                Separator()
                MenuItem("Settings…", CtrlAlt(S), onClick = onOpenSettings)
            }
        }
        Menu("Edit") {
        }
    }
}

private fun performSwingMenuAction(actionType: Int) {

}


@OptIn(ExperimentalJewelApi::class)
fun main() {
    System.setProperty("apple.awt.application.name", "Project Builder")

    application {
        // Faster scrolling in Swing components
        enableNewSwingCompositing()

        val builderState = remember { BuilderState() }

        val windowState = rememberWindowState(
            size = builderState.getWindowSize(),
            position = builderState.getWindowPosition(),
            placement = builderState.windowPlacement,
        )

        Runtime.getRuntime().addShutdownHook(Thread {
            shutdown(builderState, windowState)
        })

        val themeDefinition = if (KotlinExplorerTheme.System.isDark()) {
            JewelTheme.darkThemeDefinition()
        } else {
            JewelTheme.lightThemeDefinition()
        }
        val titleBarStyle = if (KotlinExplorerTheme.System.isDark()) {
            TitleBarStyle.dark()
        } else {
            TitleBarStyle.light()
        }

        IntUiTheme(
            themeDefinition, ComponentStyling.decoratedWindow(titleBarStyle = titleBarStyle), false
        ) {
            DecoratedWindow(
                state = windowState, onCloseRequest = {
                    builderState.setWindowState(windowState)
                    exitApplication()
                }, title = "Project Builder"
            ) {
                TitleBar(Modifier.newFullscreenControls()) {
                    Text("Project Builder")
                }
                projectBuilder(builderState)
            }
        }
    }
}

@OptIn(ExperimentalPathApi::class)
private fun shutdown(
    builderState: BuilderState, windowState: WindowState
) {
    builderState.setWindowState(windowState)
    builderState.writeState()
}

private fun BuilderState.getWindowSize() = DpSize(windowWidth.dp, windowHeight.dp)

private fun BuilderState.getWindowPosition(): WindowPosition {
    val x = windowPosX
    val y = windowPosY
    return if (x > 0 && y > 0) WindowPosition(x.dp, y.dp) else Aligned(Alignment.Center)
}

private fun BuilderState.setWindowState(windowState: WindowState) {
    windowWidth = windowState.size.width.value.toInt()
    windowHeight = windowState.size.height.value.toInt()
    windowPosX = windowState.position.x.value.toInt()
    windowPosY = windowState.position.y.value.toInt()
    windowPlacement = windowState.placement
}
