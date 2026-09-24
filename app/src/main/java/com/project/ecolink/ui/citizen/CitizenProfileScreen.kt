package com.project.ecolink.ui.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings
import com.project.ecolink.data.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    val language = AppSettings.appLanguage

    val currentThemeLabel = when (AppSettings.themeMode) {
        ThemeMode.LIGHT -> if (language == AppLanguage.FRENCH) "Mode Clair" else "Light Mode"
        ThemeMode.DARK -> if (language == AppLanguage.FRENCH) "Mode Sombre" else "Dark Mode"
        ThemeMode.SYSTEM -> if (language == AppLanguage.FRENCH) "Par défaut système" else "System Default"
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text(if (language == AppLanguage.FRENCH) "Profil" else "Profile", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    AppSettings.currentUserName.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                AppSettings.currentUserName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                AppSettings.currentUserEmail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    if (language == AppLanguage.FRENCH) "Citoyen" else "Citizen",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Theme Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showThemeDialog = true }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (language == AppLanguage.FRENCH) "Thème de l'application" else "App Theme",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        if (language == AppLanguage.FRENCH) "Sombre, Clair ou Système" else "Select Dark, Light or System mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                Text("$currentThemeLabel ▾", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f))

            // Language Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (language == AppLanguage.FRENCH) "Langue" else "Language",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        if (language == AppLanguage.FRENCH) "Langue de l'Assistant et de l'interface" else "App and AI Assistant language",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                Text("${language.displayName} ▾", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f))
        }

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(if (language == AppLanguage.FRENCH) "Se déconnecter" else "Log out", fontWeight = FontWeight.Bold)
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(if (language == AppLanguage.FRENCH) "Choisir l'apparence" else "Choose Appearance", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AppSettings.themeMode = ThemeMode.SYSTEM; showThemeDialog = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = AppSettings.themeMode == ThemeMode.SYSTEM,
                            onClick = { AppSettings.themeMode = ThemeMode.SYSTEM; showThemeDialog = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == AppLanguage.FRENCH) "Par défaut système" else "System Default")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AppSettings.themeMode = ThemeMode.LIGHT; showThemeDialog = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = AppSettings.themeMode == ThemeMode.LIGHT,
                            onClick = { AppSettings.themeMode = ThemeMode.LIGHT; showThemeDialog = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == AppLanguage.FRENCH) "Mode Clair" else "Light Mode")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AppSettings.themeMode = ThemeMode.DARK; showThemeDialog = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = AppSettings.themeMode == ThemeMode.DARK,
                            onClick = { AppSettings.themeMode = ThemeMode.DARK; showThemeDialog = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == AppLanguage.FRENCH) "Mode Sombre" else "Dark Mode")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(if (language == AppLanguage.FRENCH) "Fermer" else "Close")
                }
            }
        )
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(if (language == AppLanguage.FRENCH) "Choisir la langue" else "Choose Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AppSettings.appLanguage = AppLanguage.ENGLISH; showLanguageDialog = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = AppSettings.appLanguage == AppLanguage.ENGLISH,
                            onClick = { AppSettings.appLanguage = AppLanguage.ENGLISH; showLanguageDialog = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { AppSettings.appLanguage = AppLanguage.FRENCH; showLanguageDialog = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = AppSettings.appLanguage == AppLanguage.FRENCH,
                            onClick = { AppSettings.appLanguage = AppLanguage.FRENCH; showLanguageDialog = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Français")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(if (language == AppLanguage.FRENCH) "Fermer" else "Close")
                }
            }
        )
    }
}
