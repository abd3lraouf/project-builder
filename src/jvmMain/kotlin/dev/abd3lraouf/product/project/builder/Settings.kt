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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.*

@Composable
fun Settings(
    state: BuilderState,
    onSaveRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val storytellerSdkKotlin = remember { mutableStateOf(state.storytellerSdkKotlin) }
    val showcase = remember { mutableStateOf(state.showcase) }
    val nba = remember { mutableStateOf(state.nba) }
    val onSaveClick = {
        state.saveState(
            storytellerSdkKotlin.value,
            showcase.value,
            nba.value
        )
        onSaveRequest()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
        val toolPaths = ProjectPaths(state.directory, storytellerSdkKotlin.value, showcase.value, nba.value)
        GroupHeader("Projects")
        StringSetting("storyteller-sdk-kotlin: ", storytellerSdkKotlin) { toolPaths.isStorytellerSdkKotlinValid }
        StringSetting("storyteller-showcase-android: ", showcase) { toolPaths.isShowcaseValid }
        StringSetting("nba-nextgen-android: ", nba) { toolPaths.isNbaValid }

        Spacer(modifier = Modifier.height(8.dp))
        Buttons(saveEnabled = toolPaths.isValid, onSaveClick, onDismissRequest)
    }
}

@Composable
private fun ColumnScope.Buttons(
    saveEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.align(Alignment.End)) {
        DefaultButton(onClick = onCancelClick) {
            Text("Cancel")
        }
        DefaultButton(enabled = saveEnabled, onClick = onSaveClick) {
            Text("Save")
        }
    }
}

private fun BuilderState.saveState(
    storytellerSdkKotlin: String,
    showcase: String,
    nba: String,
) {
    this.storytellerSdkKotlin = storytellerSdkKotlin
    this.showcase = showcase
    this.nba = nba
    this.reloadToolPathsFromSettings()
}

@Composable
private fun StringSetting(title: String, state: MutableState<String>, isValid: () -> Boolean) {
    SettingRow(title, state.value, { state.value = it }, isValid)
}

@Composable
private fun ColumnScope.MultiLineStringSetting(title: String, state: MutableState<String>) {
    Row(Modifier.weight(1.0f)) {
        Text(
            title,
            modifier = Modifier
                .alignByBaseline()
                .defaultMinSize(minWidth = 200.dp),
        )
        TextArea(
            value = state.value,
            onValueChange = { state.value = it },
            modifier = Modifier
                .width(360.dp)
                .defaultMinSize(minWidth = 360.dp, minHeight = 81.dp)
                .weight(1.0f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun IntSetting(title: String, state: MutableState<String>, minValue: Int) {
    SettingRow(
        title,
        value = state.value,
        onValueChange = {
            if (it.toIntOrNull() != null || it.isEmpty()) {
                state.value = it
            }
        },
        isValid = { (state.value.toIntOrNull() ?: Int.MIN_VALUE) >= minValue }
    )
}

@Composable
private fun BooleanSetting(title: String, state: MutableState<Boolean>) {
    Row {
        Checkbox(state.value, onCheckedChange = { state.value = it })
        Text(
            title,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SettingRow(title: String, value: String, onValueChange: (String) -> Unit, isValid: () -> Boolean) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            title,
            modifier = Modifier
                .alignByBaseline()
                .defaultMinSize(minWidth = 200.dp),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .alignByBaseline()
                .defaultMinSize(minWidth = 360.dp)
                .weight(1.0f),
            trailingIcon = { if (isValid()) ValidIcon() else ErrorIcon() }
        )
    }
}

@Composable
private fun ErrorIcon() {
    Icon(
        "icons/error.svg",
        iconClass = BuilderState::class.java,
        contentDescription = "Error",
        tint = IconErrorColor
    )
}

@Composable
private fun ValidIcon() {
    Icon(
        "icons/done.svg",
        iconClass = BuilderState::class.java,
        contentDescription = "Valid",
        tint = IconValidColor
    )
}
