package com.project.ecolink.data.remote

import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EcoLinkApiClient {
    // 10.0.2.2 points to localhost of your computer from the Android Emulator
    private const val BASE_URL = "http://10.0.2.2:5000/api"
    var authToken: String? = null // Caches the JWT after login

    suspend fun login(email: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/auth/login")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }

            conn.outputStream.use { os -> os.write(jsonBody.toString().toByteArray()) }

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                if (json.has("token")) {
                    authToken = json.getString("token")
                }
                return@withContext true
            }
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext false
    }

    suspend fun submitReport(latitude: Double, longitude: Double, photoBase64: String): Boolean = withContext(Dispatchers.IO) {
        if (authToken == null) return@withContext false
        try {
            val url = URL("$BASE_URL/reports")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $authToken")
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("latitude", latitude)
                put("longitude", longitude)
                put("priority", "NORMAL")
                put("photoUrl", photoBase64)
            }

            conn.outputStream.use { os -> os.write(jsonBody.toString().toByteArray()) }
            return@withContext conn.responseCode in 200..299
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext false
    }

    suspend fun getAssignedReports(): JSONArray? = withContext(Dispatchers.IO) {
        if (authToken == null) return@withContext null
        try {
            val url = URL("$BASE_URL/reports")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $authToken")
            
            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().readText()
                return@withContext JSONArray(response)
            }
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext null
    }

    suspend fun markReportCollected(reportId: Int): Boolean = withContext(Dispatchers.IO) {
        if (authToken == null) return@withContext false
        try {
            // The backend uses PATCH /api/reports/:id/status
            val url = URL("$BASE_URL/reports/$reportId/status")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "PATCH"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $authToken")
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("status", "COLLECTED")
            }

            conn.outputStream.use { os -> os.write(jsonBody.toString().toByteArray()) }
            return@withContext conn.responseCode in 200..299
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext false
    }
}
