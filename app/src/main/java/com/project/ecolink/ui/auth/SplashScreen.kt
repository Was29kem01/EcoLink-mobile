package com.project.ecolink.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val mossPrimary = Color(0xFF2F4B3C)
    val clayAccent = Color(0xFFC4693C)
    val sandBg = Color(0xFFF4EFE6)
    val darkMoss = Color(0xFF121A15)
    val language = AppSettings.appLanguage

    // Scale animation effect on app icon
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale = transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2000L) // Display splash screen for 2 seconds before landing in app
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        sandBg,
                        Color(0xFFECE5D8),
                        sandBg
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // EcoLink Official Emblem Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale.value)
                    .shadow(16.dp, CircleShape, spotColor = mossPrimary)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(mossPrimary, Color(0xFF1E3328))
                        ),
                        shape = CircleShape
                    )
                    .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "EcoLink Logo Emblem",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enlarge Brand Title: 'Eco' in Moss Green, 'Link' in Terracotta Clay
            val splashTitle = buildAnnotatedString {
                withStyle(SpanStyle(color = mossPrimary)) { append("Eco") }
                withStyle(SpanStyle(color = clayAccent)) { append("Link") }
            }

            Text(
                text = splashTitle,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Project Slogan
            Text(
                text = if (language == AppLanguage.FRENCH) 
                    "\"Signalez vos déchets, suivez leur résolution\"" 
                else 
                    "\"Report waste, see it through\"",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = clayAccent,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Clean City Initiative • Douala & Yaoundé",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
