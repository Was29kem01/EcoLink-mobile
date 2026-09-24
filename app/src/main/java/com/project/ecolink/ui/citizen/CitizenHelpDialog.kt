package com.project.ecolink.ui.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHelpDialog(onDismiss: () -> Unit) {
    val language = AppSettings.appLanguage
    val coroutineScope = rememberCoroutineScope()
    var userQuery by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AI",
                text = if (language == AppLanguage.FRENCH)
                    "Bonjour ! Je suis EcoBot, votre assistant virtuel EcoLink. Posez une question sur la collecte des déchets, le tri sélectif ou vos signalements !"
                else
                    "Hello! I am EcoBot, your EcoLink virtual assistant. Ask me anything about waste collection schedules, sorting rules, or your local reports!"
            )
        )
    }

    val quickQuestions = remember(language) { CitizenAiAssistant.getQuickQuestions(language) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF4EFE6)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF2F4B3C).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2F4B3C), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("EcoBot", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (language == AppLanguage.FRENCH) "Assistant Assainissement EcoLink" else "EcoLink Environmental Assistant",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFC4693C),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF121A15))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )

                // Quick Prompt Chips Bar
                Surface(
                    color = Color.White.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE))
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
                        Text(
                            if (language == AppLanguage.FRENCH) "SUGGESTIONS D'INTERROGATION IA :" else "AI SUGGESTED PROMPTS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2F4B3C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(quickQuestions) { q ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF2F4B3C).copy(alpha = 0.1f),
                                    modifier = Modifier.clickable {
                                        messages.add(ChatMessage("User", q))
                                        isThinking = true
                                        coroutineScope.launch {
                                            val reply = CitizenAiAssistant.queryGemini(q, language)
                                            messages.add(ChatMessage("AI", reply))
                                            isThinking = false
                                        }
                                    }
                                ) {
                                    Text(
                                        text = q,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF2F4B3C),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.sender == "User"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isUser) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF2F4B3C), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                color = if (isUser) Color(0xFF2F4B3C) else Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    modifier = Modifier.padding(14.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isUser) Color.White else Color(0xFF121A15)
                                )
                            }
                        }
                    }

                    if (isThinking) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF2F4B3C), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (language == AppLanguage.FRENCH) "EcoBot analyse votre demande..." else "EcoBot is analyzing your request...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Input Controls Bar with Solid Black Typed Text
                Surface(
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = userQuery,
                            onValueChange = { userQuery = it },
                            placeholder = {
                                Text(
                                    if (language == AppLanguage.FRENCH) "Posez votre question à EcoBot..." else "Ask EcoBot anything about EcoLink...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            textStyle = TextStyle(color = Color(0xFF121A15), fontWeight = FontWeight.Bold, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF121A15),
                                unfocusedTextColor = Color(0xFF121A15)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (userQuery.isNotBlank()) {
                                    val text = userQuery.trim()
                                    userQuery = ""
                                    messages.add(ChatMessage("User", text))
                                    isThinking = true
                                    coroutineScope.launch {
                                        val reply = CitizenAiAssistant.queryGemini(text, language)
                                        messages.add(ChatMessage("AI", reply))
                                        isThinking = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF2F4B3C), CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
