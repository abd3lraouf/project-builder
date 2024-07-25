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

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class ProjectPaths(
    settingsDirectory: Path,
    storytellerSdkKotlin: Path,
    showcase: Path,
    nba: Path,
) {
    constructor(settingsDirectory: Path, storytellerSdkKotlin: String, showcase: String, nba: String) : this(
        settingsDirectory,
        Path.of(storytellerSdkKotlin),
        Path.of(showcase),
        Path.of(nba)
    )

    var isValid: Boolean = false
        private set
    var isStorytellerSdkKotlinValid: Boolean = false
        private set
    var isShowcaseValid: Boolean = false
        private set
    var isNbaValid: Boolean = false
        private set

    init {
        val storytellerSdkKotlinDirectory = storytellerSdkKotlin.resolve("gradlew")
        isStorytellerSdkKotlinValid = storytellerSdkKotlinDirectory.exists()
        val showcaseDirectory = showcase.resolve("gradlew")
        isShowcaseValid = showcaseDirectory.exists()
        val nbaDirectory = nba.resolve("gradlew")
        isNbaValid = nbaDirectory.exists()

        isValid =  isStorytellerSdkKotlinValid && isShowcaseValid && isNbaValid
    }
}
