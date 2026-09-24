package com.project.ecolink.ui.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenReportsScreen(modifier: Modifier = Modifier) {
    val language = AppSettings.appLanguage
    var selectedReport by remember { mutableStateOf<ReportItem?>(null) }

    val reports = remember(language) {
        if (language == AppLanguage.FRENCH) listOf(
            ReportItem("101", "Rue 1.234, Emombo, Yaoundé", "Aujourd'hui, 08:14", "En attente", "Plastiques & Bouteilles", "Paul Biya (Agent #05)", Color(0xFF7A8272), Color(0xFFEDEAE1)),
            ReportItem("102", "Carrefour Nlongkak, Yaoundé", "Hier, 17:40", "Affecté", "Bac débordant", "Samuel Eto (Agent #06)", Color(0xFF8A6119), Color(0xFFFBF1DE)),
            ReportItem("103", "Marché Mokolo, entrée B", "Il y a 3 jours", "Collecté", "Dépôt sauvage", "Francis Ngannou (Agent #07)", Color(0xFF4E8B5C), Color(0xFFE4F0E5))
        ) else listOf(
            ReportItem("101", "Rue 1.234, Emombo, Yaoundé", "Today, 08:14 AM", "Pending", "Plastic & Bottled waste", "Paul Biya (Agent #05)", Color(0xFF7A8272), Color(0xFFEDEAE1)),
            ReportItem("102", "Carrefour Nlongkak, Yaoundé", "Yesterday, 05:40 PM", "Assigned", "Overflowing bin", "Samuel Eto (Agent #06)", Color(0xFF8A6119), Color(0xFFFBF1DE)),
            ReportItem("103", "Marché Mokolo, Entrance B", "3 days ago", "Collected", "Illegal dumping", "Francis Ngannou (Agent #07)", Color(0xFF4E8B5C), Color(0xFFE4F0E5))
        )
    }

    // Interactive Report Detail & Tracking Timeline Modal
    if (selectedReport != null) {
        Dialog(onDismissRequest = { selectedReport = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                if (language == AppLanguage.FRENCH) "Signalement #${selectedReport!!.id}" else "Report #${selectedReport!!.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2F4B3C)
                            )
                            Text(
                                selectedReport!!.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC4693C),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { selectedReport = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF4EFE6),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2F4B3C))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(selectedReport!!.location, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text(
                                    if (language == AppLanguage.FRENCH) "Signalé le : ${selectedReport!!.time}" else "Reported on: ${selectedReport!!.time}", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tracking Timeline
                    Text(
                        if (language == AppLanguage.FRENCH) "HISTORIQUE DU SIGNALEMENT" else "REPORT TRACKING TIMELINE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimelineStep(
                            if (language == AppLanguage.FRENCH) "1. Signalement Reçu" else "1. Report Received", 
                            if (language == AppLanguage.FRENCH) "Transmis à la station locale" else "Queued at local dispatch station", 
                            true
                        )
                        TimelineStep(
                            if (language == AppLanguage.FRENCH) "2. Vérification GPS & Contenu" else "2. GPS & Incident Verification", 
                            if (language == AppLanguage.FRENCH) "Validé par le contrôleur" else "Validated by station manager", 
                            true
                        )
                        TimelineStep(
                            if (language == AppLanguage.FRENCH) "3. Assignation Agent Terrain" else "3. Field Agent Assignment", 
                            if (language == AppLanguage.FRENCH) "Assigné à ${selectedReport!!.assignedAgent}" else "Assigned to ${selectedReport!!.assignedAgent}", 
                            selectedReport!!.status.lowercase().contains("assign") || selectedReport!!.status.lowercase().contains("affect") || selectedReport!!.status.lowercase().contains("collect")
                        )
                        TimelineStep(
                            if (language == AppLanguage.FRENCH) "4. Collecte & Résolution" else "4. Collection & Resolution", 
                            if (language == AppLanguage.FRENCH) "Nettoyage du site complété" else "Site cleanup completed", 
                            selectedReport!!.status.lowercase().contains("collect")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedReport = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4B3C))
                    ) {
                        Text(if (language == AppLanguage.FRENCH) "Fermer Suivi" else "Close Tracker")
                    }
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text(if (language == AppLanguage.FRENCH) "Mes Signalements EcoLink" else "My EcoLink Reports", fontWeight = FontWeight.Bold) })

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reports) { report ->
                Surface(
                    onClick = { selectedReport = report },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF2F4B3C).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFF2F4B3C))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(report.location, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF121A15))
                            Text("#${report.id} • ${report.time}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Surface(color = report.badgeBg, shape = RoundedCornerShape(20.dp)) {
                            Text(report.status, color = report.badgeColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineStep(title: String, subtitle: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(if (isDone) Color(0xFF4E8B5C) else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isDone) Color(0xFF121A15) else Color.Gray)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

data class ReportItem(
    val id: String,
    val location: String, 
    val time: String, 
    val status: String,
    val category: String,
    val assignedAgent: String,
    val badgeColor: Color, 
    val badgeBg: Color
)
