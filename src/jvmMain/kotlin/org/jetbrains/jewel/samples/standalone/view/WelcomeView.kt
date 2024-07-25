package org.jetbrains.jewel.samples.standalone.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import dev.abd3lraouf.product.project.builder.BuilderState
import dev.abd3lraouf.product.project.builder.Settings
import org.jetbrains.jewel.foundation.modifier.trackActivation
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.samples.standalone.viewmodel.View
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.PlatformIcon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.hints.Size
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
@View(title = "Welcome", position = 0, icon = "icons/meetNewUi.svg")
fun WelcomeView() {
    var showSettings by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.trackActivation().fillMaxSize().background(JewelTheme.globalColors.panelBackground)
    ) {
        Tooltip(modifier = Modifier.align(Alignment.TopEnd), tooltip = { Text("Settings") }) {
            IconButton(onClick = {
                showSettings = true
            }, modifier = Modifier.size(40.dp).padding(5.dp)) {
                PlatformIcon(AllIconsKeys.General.Settings, "Settings", hint = Size(20))
            }
        }

        if (showSettings) {
            SettingsDialog(BuilderState()) {
                showSettings = false
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    builderState: BuilderState,
    onDismissRequest: () -> Unit
) {
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
//        fileChooserDialog("Choose a file")
//        Settings(builderState, onSaveRequest = onSaveRequest, onDismissRequest = onDismissRequest)
    }

}

fun fileChooserDialog(
    title: String?
): String {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    fileChooser.isAcceptAllFileFilterUsed = true
    fileChooser.selectedFile = null
    fileChooser.currentDirectory = null
    val file = if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.toString()
    } else {

        ""

    }

    return file

}
