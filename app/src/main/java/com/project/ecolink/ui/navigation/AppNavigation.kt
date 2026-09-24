package com.project.ecolink.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.ecolink.ui.agent.AgentDashboard
import com.project.ecolink.ui.agent.AgentResolutionScreen
import com.project.ecolink.ui.auth.LoginScreen
import com.project.ecolink.ui.auth.RegisterScreen
import com.project.ecolink.ui.auth.SplashScreen
import com.project.ecolink.ui.citizen.CitizenDashboard

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("SPLASH") }
    var userRole by remember { mutableStateOf<String?>(null) }
    var activeAssignmentId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "SPLASH" -> SplashScreen(
            onSplashFinished = { currentScreen = "LOGIN" }
        )
        "LOGIN" -> LoginScreen(
            onLoginSuccess = { role ->
                userRole = role
                currentScreen = if (role == "FIELD_AGENT") "AGENT_HOME" else "CITIZEN_HOME"
            },
            onNavigateToRegister = { currentScreen = "REGISTER" }
        )
        "REGISTER" -> RegisterScreen(
            onRegisterSuccess = { role ->
                userRole = role
                currentScreen = if (role == "FIELD_AGENT_PENDING") "PENDING_APPROVAL" else "CITIZEN_HOME"
            },
            onNavigateToLogin = { currentScreen = "LOGIN" }
        )
        "CITIZEN_HOME" -> {
            CitizenDashboard(onLogout = { currentScreen = "LOGIN"; userRole = null })
        }
        "AGENT_HOME" -> {
            AgentDashboard(
                onNavigateToResolution = { assignmentId ->
                    activeAssignmentId = assignmentId
                    currentScreen = "AGENT_RESOLUTION"
                },
                onLogout = { currentScreen = "LOGIN"; userRole = null }
            )
        }
        "AGENT_RESOLUTION" -> {
            AgentResolutionScreen(
                assignmentId = activeAssignmentId ?: "",
                onBack = { currentScreen = "AGENT_HOME" },
                onResolutionComplete = { currentScreen = "AGENT_HOME" }
            )
        }
        "PENDING_APPROVAL" -> {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Your Field Agent application is pending approval by a Station Admin.")
            }
        }
    }
}
