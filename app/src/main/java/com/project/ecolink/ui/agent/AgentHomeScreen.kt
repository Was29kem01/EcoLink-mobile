package com.project.ecolink.ui.agent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentHomeScreen(modifier: Modifier = Modifier, onNavigateToResolution: (String) -> Unit) {
    val assignments = remember {
        listOf(
            AssignmentItem("1", "Carrefour Nlongkak", "Overflowing bin", "Yesterday, 5:40 PM", "High"),
            AssignmentItem("2", "Marché Mokolo, entrée B", "Illegal dumping", "Today, 8:00 AM", "Critical"),
            AssignmentItem("3", "Avenue Foch, Akwa", "Litter on sidewalk", "2 days ago", "Medium")
        )
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("My Assignments", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(assignments) { assignment ->
                AssignmentCard(assignment = assignment, onResolveClick = { onNavigateToResolution(assignment.id) })
            }
        }
    }
}

@Composable
fun AssignmentCard(assignment: AssignmentItem, onResolveClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE)),
        shadowElevation = if (expanded) 4.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2F4B3C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(assignment.location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(assignment.category, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Surface(
                    color = if (assignment.urgency == "Critical") Color(0xFFFDE8E8) else Color(0xFFFBF1DE),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = assignment.urgency,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (assignment.urgency == "Critical") Color(0xFFC81E1E) else Color(0xFF8A6119),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) {
                    HorizontalDivider(color = Color(0xFFE4DDCE), modifier = Modifier.padding(bottom = 16.dp))
                    
                    // Mocking the Map/Route view
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEAF0E7)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("??? Live Route to ", color = Color(0xFF2F4B3C), fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Reported: ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onResolveClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC4693C))
                    ) {
                        Text("Mark as Resolved (Take Photo)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class AssignmentItem(val id: String, val location: String, val category: String, val time: String, val urgency: String)
