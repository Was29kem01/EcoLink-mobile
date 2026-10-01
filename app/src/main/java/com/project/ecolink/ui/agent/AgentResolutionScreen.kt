package com.project.ecolink.ui.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentResolutionScreen(assignmentId: String, onBack: () -> Unit, onResolutionComplete: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Resolve Assignment", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Proof of Collection", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Take a clear photo of the cleaned area to verify resolution.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            
            Spacer(modifier = Modifier.height(48.dp))

            // Big Camera Button for After Photo
            Surface(
                modifier = Modifier.size(160.dp),
                shape = CircleShape,
                color = Color(0xFF2F4B3C),
                shadowElevation = 8.dp,
                onClick = { /* Mock taking photo */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Take Photo",
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Tap to open camera", color = Color.Gray, style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.weight(1f))

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    // Launch real Google Maps Navigation Intent to the report location
                    // Using default Yaounde coordinates for testing the MVP
                    val gmmIntentUri = android.net.Uri.parse("google.navigation:q=3.8480,11.5021")
                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        // Fallback if Google Maps is not installed
                        val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=3.8480,11.5021"))
                        context.startActivity(browserIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC4693C))
            ) {
                // Removed LocationOn icon to fix compile error
                Text("Start GPS Navigation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onResolutionComplete,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E8B5C))
            ) {
                Text("Submit Proof & Close Ticket", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
