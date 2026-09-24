package com.project.ecolink.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var activeLanguage by remember { mutableStateOf(AppSettings.appLanguage) }

    // Validation & Error Alert State
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Password Reset Verification Modal State
    var show2FaModal by remember { mutableStateOf(false) }
    var twoFaStep by remember { mutableStateOf(1) } // 1: Send OTP, 2: Verify OTP, 3: Success
    var otpCode by remember { mutableStateOf("") }
    var recoveryEmail by remember { mutableStateOf("") }
    var countdownTimer by remember { mutableStateOf(30) }

    // 30-Second Countdown Timer for Code Resend
    LaunchedEffect(twoFaStep, countdownTimer) {
        if (twoFaStep == 2 && countdownTimer > 0) {
            kotlinx.coroutines.delay(1000L)
            countdownTimer--
        }
    }

    val mossPrimary = Color(0xFF2F4B3C)
    val clayAccent = Color(0xFFC4693C)
    val sandBg = Color(0xFFF4EFE6)
    val darkMoss = Color(0xFF121A15)

    // Error Alert Dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            icon = { Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFB7503A), modifier = Modifier.size(36.dp)) },
            title = { Text(if (activeLanguage == AppLanguage.FRENCH) "Erreur d'authentification" else "Authentication Error", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage!!, color = darkMoss) },
            confirmButton = {
                Button(
                    onClick = { errorMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = mossPrimary)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Password Reset Code Verification Modal Dialog
    if (show2FaModal) {
        Dialog(onDismissRequest = { show2FaModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(54.dp).background(mossPrimary.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = mossPrimary, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        if (activeLanguage == AppLanguage.FRENCH) "Réinitialisation du Mot de Passe" else "Password Reset Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = darkMoss
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    when (twoFaStep) {
                        1 -> {
                            Text(
                                if (activeLanguage == AppLanguage.FRENCH) "Entrez l'email ou le téléphone de votre compte pour recevoir un code de vérification à 6 chiffres." else "Enter your account email/phone to receive a 6-digit verification code.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = recoveryEmail,
                                onValueChange = { recoveryEmail = it },
                                label = { Text("Email / Phone") },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = mossPrimary) },
                                textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    if (recoveryEmail.isNotBlank()) {
                                        twoFaStep = 2
                                        countdownTimer = 30
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = mossPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(if (activeLanguage == AppLanguage.FRENCH) "Envoyer le Code" else "Send Code")
                            }
                        }
                        2 -> {
                            Text(
                                if (activeLanguage == AppLanguage.FRENCH) "Saisissez le code à 6 chiffres envoyé à ${recoveryEmail.ifEmpty { "votre contact" }}." else "Enter the 6-digit verification code sent to ${recoveryEmail.ifEmpty { "your contact" }}.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("Code de Vérification (ex: 849201)") },
                                leadingIcon = { Icon(Icons.Default.LockReset, null, tint = clayAccent) },
                                textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 30-Second Countdown & Resend Code Trigger
                            if (countdownTimer > 0) {
                                Text(
                                    text = if (activeLanguage == AppLanguage.FRENCH) "Renvoyer le code dans ${countdownTimer}s" else "Resend code in ${countdownTimer}s",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = if (activeLanguage == AppLanguage.FRENCH) "Renvoyer le code" else "Resend Code",
                                    fontSize = 13.sp,
                                    color = clayAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        countdownTimer = 30
                                        otpCode = ""
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    if (otpCode.length >= 4) {
                                        twoFaStep = 3 
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = clayAccent),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(if (activeLanguage == AppLanguage.FRENCH) "Vérifier le Code" else "Verify Code")
                            }
                        }
                        3 -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4E8B5C), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (activeLanguage == AppLanguage.FRENCH) "Code Vérifié avec Succès !" else "Code Verified Successfully!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4E8B5C)
                            )
                            Text(
                                if (activeLanguage == AppLanguage.FRENCH) "Accès au tableau de bord accordé." else "Access granted to user dashboard.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // POST-VERIFICATION MOVE: Direct Login & Navigate to User Dashboard
                            Button(
                                onClick = { 
                                    show2FaModal = false
                                    twoFaStep = 1
                                    onLoginSuccess("CLIENT")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = mossPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(if (activeLanguage == AppLanguage.FRENCH) "Accéder au Tableau de Bord" else "Open Dashboard")
                            }
                        }
                    }
                }
            }
        }
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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Language Selector Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        activeLanguage = if (activeLanguage == AppLanguage.FRENCH) AppLanguage.ENGLISH else AppLanguage.FRENCH
                        AppSettings.appLanguage = activeLanguage
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = mossPrimary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, mossPrimary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = mossPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (activeLanguage == AppLanguage.FRENCH) "FR (Passer en EN)" else "EN (Switch to FR)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = mossPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // EcoLink Brand Hero Header (Clean Emblem Logo)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(12.dp, CircleShape, spotColor = mossPrimary)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(mossPrimary, Color(0xFF1E3328))
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "EcoLink Vector Emblem",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Color Title: 'Eco' in Moss Green, 'Link' in Terracotta Clay
            val dualColorTitle = buildAnnotatedString {
                withStyle(SpanStyle(color = mossPrimary)) { append("Eco") }
                withStyle(SpanStyle(color = clayAccent)) { append("Link") }
            }

            Text(
                text = dualColorTitle,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = if (activeLanguage == AppLanguage.FRENCH) 
                    "Signalez vos déchets, suivez leur résolution" 
                else 
                    "Report waste, see it through",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = clayAccent,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Main Glassmorphism Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5DFD3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (activeLanguage == AppLanguage.FRENCH) "Connexion" else "Sign In",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkMoss
                    )

                    Text(
                        text = if (activeLanguage == AppLanguage.FRENCH) 
                            "Entrez vos identifiants pour continuer" 
                        else 
                            "Access your account or administrative portal",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Quick Role Fill Chips
                    Text(
                        text = "⚡ Quick Test Role Auto-Fill:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = mossPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = email.contains("citizen"),
                            onClick = {
                                email = "citizen@ecolink.cm"
                                password = "password123"
                            },
                            label = { Text("Citizen", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(12.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = mossPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = mossPrimary
                            )
                        )
                        FilterChip(
                            selected = email.contains("agent"),
                            onClick = {
                                email = "agent@ecolink.cm"
                                password = "password123"
                            },
                            label = { Text("Agent", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(12.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = clayAccent.copy(alpha = 0.15f),
                                selectedLabelColor = clayAccent
                            )
                        )
                    }

                    // Email Field with Solid Black Typed Text Visibility
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (activeLanguage == AppLanguage.FRENCH) "Adresse Email" else "Email Address") },
                        placeholder = { Text("name@domain.com") },
                        textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = mossPrimary
                            )
                        },
                        trailingIcon = {
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(Icons.Default.Clear, null, tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mossPrimary,
                            unfocusedBorderColor = Color(0xFFD6D0C4),
                            focusedLabelColor = mossPrimary,
                            focusedTextColor = darkMoss,
                            unfocusedTextColor = darkMoss
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field with Solid Black Typed Text Visibility
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (activeLanguage == AppLanguage.FRENCH) "Mot de passe" else "Password") },
                        placeholder = { Text("••••••••") },
                        textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = mossPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mossPrimary,
                            unfocusedBorderColor = Color(0xFFD6D0C4),
                            focusedLabelColor = mossPrimary,
                            focusedTextColor = darkMoss,
                            unfocusedTextColor = darkMoss
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options Row: Remember Me & 2FA Forgot Password Trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = mossPrimary)
                            )
                            Text(
                                text = if (activeLanguage == AppLanguage.FRENCH) "Se souvenir" else "Remember me",
                                fontSize = 12.sp,
                                color = darkMoss
                            )
                        }

                        Text(
                            text = if (activeLanguage == AppLanguage.FRENCH) "Mot de passe oublié (2FA)?" else "Forgot password (2FA)?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = clayAccent,
                            modifier = Modifier.clickable {
                                recoveryEmail = email
                                show2FaModal = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Submit Button with Strict Input Validation Error Catch
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = if (activeLanguage == AppLanguage.FRENCH) 
                                    "Veuillez saisir votre adresse email et votre mot de passe pour vous connecter !" 
                                else 
                                    "Please enter your email address and password to log in!"
                            } else {
                                isLoading = true
                                val role = if (email.contains("agent")) "FIELD_AGENT" else "CLIENT"
                                onLoginSuccess(role)
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = mossPrimary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (activeLanguage == AppLanguage.FRENCH) "Se Connecter" else "Log In to EcoLink",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Register/Franchise Prompt Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToRegister() },
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDFD8CC))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(clayAccent.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = clayAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (activeLanguage == AppLanguage.FRENCH) 
                                    "Nouveau sur EcoLink?" 
                                else 
                                    "New to EcoLink?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkMoss
                            )
                            Text(
                                text = if (activeLanguage == AppLanguage.FRENCH) 
                                    "Créer un compte citoyen ou postuler" 
                                else 
                                    "Create citizen account or apply as agent",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = mossPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Tagline
            Text(
                text = "EcoLink Waste Management Infrastructure © 2026",
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
