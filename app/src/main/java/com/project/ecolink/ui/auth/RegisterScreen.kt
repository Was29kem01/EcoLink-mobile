package com.project.ecolink.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: (String) -> Unit, onNavigateToLogin: () -> Unit) {
    val language = AppSettings.appLanguage

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("Bastos, Yaoundé") }

    var isSubmitting by remember { mutableStateOf(false) }

    val mossPrimary = Color(0xFF2F4B3C)
    val clayAccent = Color(0xFFC4693C)
    val sandBg = Color(0xFFF4EFE6)
    val darkMoss = Color(0xFF121A15)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(sandBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = darkMoss)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(mossPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        val dualColorRegisterTitle = buildAnnotatedString {
            withStyle(SpanStyle(color = mossPrimary)) { append("Eco") }
            withStyle(SpanStyle(color = clayAccent)) { append("Link ") }
            withStyle(SpanStyle(color = darkMoss)) { append(if (language == AppLanguage.FRENCH) "Inscription" else "Registration") }
        }

        Text(text = dualColorRegisterTitle, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            if (language == AppLanguage.FRENCH) "Rejoignez la plateforme nationale d'assainissement" else "Join the national waste management network",
            fontSize = 12.sp,
            color = clayAccent,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (language == AppLanguage.FRENCH) "Nom complet" else "Full Name") },
                    textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = mossPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = mossPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (language == AppLanguage.FRENCH) "Téléphone (+237)" else "Phone (+237)") },
                    textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = mossPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = sector,
                    onValueChange = { sector = it },
                    label = { Text(if (language == AppLanguage.FRENCH) "Quartier / Secteur de résidence" else "Sector / Quarter") },
                    textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    leadingIcon = { Icon(Icons.Default.HomeWork, null, tint = mossPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                // Password Field with Eye Visibility Toggle Icon
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (language == AppLanguage.FRENCH) "Mot de passe" else "Password") },
                    textStyle = TextStyle(color = darkMoss, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = mossPrimary) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password Visibility",
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        isSubmitting = true
                        AppSettings.currentUserName = fullName.ifEmpty { "New Citizen" }
                        AppSettings.currentUserEmail = email.ifEmpty { "citizen@propre.com" }
                        AppSettings.currentUserPhone = phone
                        AppSettings.currentUserSector = sector
                        onRegisterSuccess("CLIENT")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mossPrimary),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        if (language == AppLanguage.FRENCH) "Créer un Compte Citoyen" else "Create Citizen Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
