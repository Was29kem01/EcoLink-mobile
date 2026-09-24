package com.project.ecolink.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    FRENCH("Français", "fr")
}

object AppSettings {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    var appLanguage by mutableStateOf(AppLanguage.ENGLISH)

    var currentUserName by mutableStateOf("John Citizen")
    var currentUserEmail by mutableStateOf("citizen@propre.com")
    var currentUserPhone by mutableStateOf("")
    var currentUserSector by mutableStateOf("")

    val isDarkMode: Boolean?
        get() = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> null
        }
}
