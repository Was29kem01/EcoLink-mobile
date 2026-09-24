package com.project.ecolink.ui.citizen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings

@Composable
fun CitizenDashboard(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val language = AppSettings.appLanguage

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(if (language == AppLanguage.FRENCH) "Accueil" else "Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "My Reports") },
                    label = { Text(if (language == AppLanguage.FRENCH) "Mes Signalements" else "My Reports") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(if (language == AppLanguage.FRENCH) "Profil" else "Profile") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        when (selectedTab) {
            0 -> CitizenHomeScreen(modifier = modifier, onOpenAiHelp = { showHelpDialog = true })
            1 -> CitizenReportsScreen(modifier)
            2 -> CitizenProfileScreen(modifier, onLogout)
        }
    }

    if (showHelpDialog) {
        CitizenHelpDialog(onDismiss = { showHelpDialog = false })
    }
}
