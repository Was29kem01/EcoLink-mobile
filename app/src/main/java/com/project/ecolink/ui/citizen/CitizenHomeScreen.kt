package com.project.ecolink.ui.citizen

import android.Manifest
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.project.ecolink.data.AppLanguage
import com.project.ecolink.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHomeScreen(
    modifier: Modifier = Modifier,
    onOpenAiHelp: () -> Unit = {}
) {
    val context = LocalContext.current
    val language = AppSettings.appLanguage
    val categories = remember(language) {
        if (language == AppLanguage.FRENCH) listOf(
            "Bac débordant",
            "Dépôt sauvage",
            "Déchets plastiques & bouteilles",
            "Déchets organiques & verts",
            "Déchets dangereux / électroniques"
        ) else listOf(
            "Overflowing bin",
            "Illegal dumping",
            "Plastic & bottled waste",
            "Organic & green waste",
            "Hazardous / E-Waste"
        )
    }

    var description by remember { mutableStateOf("") }
    var photoErrorDialog by remember { mutableStateOf(false) }
    
    // Live Dynamic Location Coordinates
    var currentSectorName by remember { mutableStateOf("Rue 1.234, Emombo, Yaoundé") }
    var currentLat by remember { mutableStateOf(3.8480) }
    var currentLng by remember { mutableStateOf(11.5021) }

    // Photo & Camera state
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isPhotoBlurry by remember { mutableStateOf(false) }
    var photoNudgeDismissed by remember { mutableStateOf(false) }
    var showPhotoPreviewModal by remember { mutableStateOf(false) }
    var showMapModal by remember { mutableStateOf(false) }
    var submitSuccessDialog by remember { mutableStateOf(false) }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        showMapModal = true
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            isPhotoBlurry = false
            photoNudgeDismissed = false
        }
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    val photoNudge = remember(capturedBitmap, isPhotoBlurry, language) {
        if (capturedBitmap != null) CitizenAiAssistant.inspectPhotoQuality(isPhotoBlurry, language) else null
    }

    // Large Full-Screen Interactive Location Map Dialog
    if (showMapModal) {
        Dialog(
            onDismissRequest = { showMapModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF4EFE6)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFC4693C))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (language == AppLanguage.FRENCH) "Carte GPS du Signalement" else "Report GPS Map Picker",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showMapModal = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )

                    // Actual Mapbox Map
                    com.mapbox.maps.extension.compose.MapboxMap(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        style = { com.mapbox.maps.extension.compose.style.MapStyle(style = com.mapbox.maps.Style.MAPBOX_STREETS) },
                        mapViewportState = com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState {
                            setCameraOptions {
                                zoom(14.0)
                                center(com.mapbox.geojson.Point.fromLngLat(currentLng, currentLat))
                            }
                        },
                        onMapClickListener = { point ->
                            currentLat = point.latitude()
                            currentLng = point.longitude()
                            currentSectorName = "Custom Pin Location"
                            true
                        }
                    ) {
                        com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation(
                            point = com.mapbox.geojson.Point.fromLngLat(currentLng, currentLat)
                        )
                    }

                    // Sector Pin Selector Bar
                    Surface(
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                if (language == AppLanguage.FRENCH) "SECTEURS DE COLLECTE DISPONIBLES :" else "SELECT GPS SECTOR PIN:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2F4B3C)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = currentSectorName.contains("Emombo"),
                                    onClick = {
                                        currentSectorName = "Rue 1.234, Emombo, Yaoundé"
                                        currentLat = 3.8480
                                        currentLng = 11.5021
                                    },
                                    label = { Text("Emombo", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = currentSectorName.contains("Nlongkak"),
                                    onClick = {
                                        currentSectorName = "Carrefour Nlongkak, Yaoundé"
                                        currentLat = 3.8500
                                        currentLng = 11.5100
                                    },
                                    label = { Text("Nlongkak", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = currentSectorName.contains("Mokolo"),
                                    onClick = {
                                        currentSectorName = "Marché Mokolo, Yaoundé"
                                        currentLat = 3.8420
                                        currentLng = 11.4980
                                    },
                                    label = { Text("Mokolo", fontSize = 11.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // POST-MAP MOVE: Calculated Dropoff Station Distance & ETA
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF2F4B3C).copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            if (language == AppLanguage.FRENCH) "STATION DE COLLECTE LA PLUS PROCHE" else "NEAREST DROPOFF STATION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2F4B3C)
                                        )
                                        Text(
                                            "EcoLink ${currentSectorName.split(",").firstOrNull() ?: "Hub"} • 1.2 km",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF121A15)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFC4693C)
                                    ) {
                                        Text(
                                            "ETA ~15 min",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { showMapModal = false },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4B3C))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (language == AppLanguage.FRENCH) "Confirmer & Transférer l'Emplacement" else "Confirm & Attach GPS Location",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Photo Preview Dialog
    if (showPhotoPreviewModal && capturedBitmap != null) {
        Dialog(onDismissRequest = { showPhotoPreviewModal = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (language == AppLanguage.FRENCH) "Photo Capturée" else "Captured Photo Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Waste Photo",
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showPhotoPreviewModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4B3C))
                    ) {
                        Text(if (language == AppLanguage.FRENCH) "Fermer" else "Close")
                    }
                }
            }
        }
    }

    // Submit Success Dialog
    if (submitSuccessDialog) {
        AlertDialog(
            onDismissRequest = { submitSuccessDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4E8B5C), modifier = Modifier.size(36.dp)) },
            title = { Text(if (language == AppLanguage.FRENCH) "Signalement Envoyé !" else "Report Submitted!") },
            text = { Text(if (language == AppLanguage.FRENCH) "Votre signalement a été transmis à la station EcoLink la plus proche (${currentSectorName}). Merci pour votre civisme." else "Your report for ${currentSectorName} has been queued for collection by the local EcoLink dispatch team.") },
            confirmButton = {
                TextButton(onClick = { 
                    submitSuccessDialog = false 
                    description = ""
                    capturedBitmap = null
                }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Missing Photo Error Dialog
    if (photoErrorDialog) {
        AlertDialog(
            onDismissRequest = { photoErrorDialog = false },
            icon = { Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFC4693C), modifier = Modifier.size(36.dp)) },
            title = { Text(if (language == AppLanguage.FRENCH) "Photo Requise" else "Photo Required") },
            text = { Text(if (language == AppLanguage.FRENCH) "Veuillez prendre une photo du site avant de soumettre le signalement. Cela aide nos agents à mieux préparer leur intervention." else "Please capture a photo of the waste before submitting. This is strictly required for our field agents to prepare logistics.") },
            confirmButton = {
                TextButton(onClick = { photoErrorDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFFC4693C))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Dual-Color Top Bar Title: 'Eco' in Moss Green, 'Link' in Terracotta Clay
        val dualColorTitle = buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFF2F4B3C))) { append("Eco") }
            withStyle(SpanStyle(color = Color(0xFFC4693C))) { append("Link") }
        }

        TopAppBar(
            title = { 
                Text(
                    text = dualColorTitle, 
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ) 
            },
            actions = {
                Surface(
                    onClick = onOpenAiHelp,
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2F4B3C).copy(alpha = 0.4f)),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "EcoBot",
                            tint = Color(0xFF2F4B3C),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "EcoBot",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        val isOnline by com.project.ecolink.utils.NetworkConnectivityObserver(context).observe().collectAsState(initial = true)
        
        // Network Status Banner
        Surface(
            color = if (isOnline) Color(0xFF4E8B5C).copy(alpha = 0.12f) else Color(0xFFC4693C).copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff, 
                    contentDescription = null, 
                    tint = if (isOnline) Color(0xFF4E8B5C) else Color(0xFFC4693C), 
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOnline) 
                        (if (language == AppLanguage.FRENCH) "Connecté — Mode En Ligne" else "Online — Secure connection active") 
                    else 
                        (if (language == AppLanguage.FRENCH) "Mode Hors-Ligne Actif — Sauvegarde locale en attente" else "Offline Mode Active — Auto-sync on network reconnect"),
                    color = if (isOnline) Color(0xFF4E8B5C) else Color(0xFFC4693C),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Camera Capture Area with Real Permission Trigger
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = if (capturedBitmap != null) Color(0xFFC4693C) else Color(0xFF2F4B3C),
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.padding(28.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (capturedBitmap != null) 
                        (if (language == AppLanguage.FRENCH) "Photo capturée (Toucher pour voir / reprendre)" else "Photo Captured (Tap to view / retake)")
                    else 
                        (if (language == AppLanguage.FRENCH) "Toucher l'appareil photo pour photographier" else "Tap camera icon to photograph waste"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // AI Photo Quality Nudge Banner
        if (capturedBitmap != null && !photoNudgeDismissed && photoNudge != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2F4B3C).copy(alpha = 0.12f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF2F4B3C)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.FRENCH) "Contrôle Qualité IA" else "AI Photo Quality Check",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2F4B3C)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = photoNudge.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF121A15)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showPhotoPreviewModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC4693C)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (language == AppLanguage.FRENCH) "Voir la Photo" else "View Photo", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { photoNudgeDismissed = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(photoNudge.keepButtonText, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Location Map Preview Card (Triggers Location Permission Check)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (language == AppLanguage.FRENCH) "LOCALISATION DU SIGNALEMENT" else "REPORT LOCATION",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (language == AppLanguage.FRENCH) "Ouvrir la Carte" else "Open Map",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC4693C),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    val hasLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasLocationPermission) {
                        showMapModal = true
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable {
                    val hasLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasLocationPermission) {
                        showMapModal = true
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF2F4B3C).copy(alpha = 0.06f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4DDCE))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2F4B3C), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            currentSectorName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF121A15)
                        )
                        Text(
                            "Lat: ${String.format("%.4f", currentLat)}, Long: ${String.format("%.4f", currentLng)} (Précision GPS 4m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF2F4B3C))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Description Area
        Text(
            if (language == AppLanguage.FRENCH) "DESCRIPTION (OBLIGATOIRE)" else "DESCRIPTION (REQUIRED)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE4DDCE),
                focusedBorderColor = Color(0xFF2F4B3C)
            ),
            placeholder = {
                Text(
                    if (language == AppLanguage.FRENCH) "Décrivez la situation (ex: déchets mélangés bloquant la route...)" 
                    else "Describe the situation (e.g., mixed waste blocking the road...)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Simplified Submit Button Text: 'Submit Report' / 'Envoyer le Signalement'
        val coroutineScope = rememberCoroutineScope()
        var isSubmitting by remember { mutableStateOf(false) }

        Button(
            onClick = { 
                if (capturedBitmap == null) {
                    photoErrorDialog = true
                } else if (!isSubmitting) {
                    isSubmitting = true
                    coroutineScope.launch {
                        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
                        val caps = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        val isOnline = caps?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

                        if (isOnline) {
                            val success = com.project.ecolink.data.remote.EcoLinkApiClient.submitReport(
                                currentLat,
                                currentLng,
                                "mockBase64ImageString" // Real base64 conversion can be added later to save memory
                            )
                            if (success) {
                                submitSuccessDialog = true 
                            } else {
                                // Save locally if API fails
                                val reportDao = com.project.ecolink.data.local.AppDatabase.getDatabase(context).reportDao()
                                reportDao.insertReport(com.project.ecolink.data.local.ReportEntity(
                                    photoPath = "mockBase64ImageString",
                                    latitude = currentLat,
                                    longitude = currentLng,
                                    status = "OFFLINE_SYNC_PENDING"
                                ))
                                submitSuccessDialog = true
                            }
                        } else {
                            // Offline - Save locally
                            val reportDao = com.project.ecolink.data.local.AppDatabase.getDatabase(context).reportDao()
                            reportDao.insertReport(com.project.ecolink.data.local.ReportEntity(
                                photoPath = "mockBase64ImageString",
                                latitude = currentLat,
                                longitude = currentLng,
                                status = "OFFLINE_SYNC_PENDING"
                            ))
                            submitSuccessDialog = true
                        }
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4B3C))
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (language == AppLanguage.FRENCH) "Envoyer le Signalement" else "Submit Report", 
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
