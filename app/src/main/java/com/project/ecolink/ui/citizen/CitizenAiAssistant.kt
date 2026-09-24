package com.project.ecolink.ui.citizen

import com.project.ecolink.data.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class FaqItem(
    val id: String,
    val questionEn: String,
    val questionFr: String,
    val answerEn: String,
    val answerFr: String
)

data class PhotoNudgeResult(
    val title: String,
    val message: String,
    val isQualityGood: Boolean,
    val retakeButtonText: String,
    val keepButtonText: String
)

object CitizenAiAssistant {

    // Primary Backend URL (10.0.2.2 for Android Emulator, localhost/IP for physical phone)
    private const val BACKEND_URL = "http://10.0.2.2:5000/api/ai"

    val faqList = listOf(
        FaqItem(
            id = "submit",
            questionEn = "How do I submit a report?",
            questionFr = "Comment signaler des déchets ?",
            answerEn = "Tap the Camera icon on the Home screen to capture or select a photo of the waste. Adjust your location pin on the map preview, choose a category (e.g. Overflowing Bin), and tap 'Submit Report'.",
            answerFr = "Appuyez sur l'icône Appareil photo sur l'écran d'accueil pour prendre une photo. Ajustez votre position sur la carte, choisissez une catégorie et appuyez sur 'Envoyer le signalement'."
        ),
        FaqItem(
            id = "status",
            questionEn = "What do report statuses mean?",
            questionFr = "Que signifient les statuts du signalement ?",
            answerEn = "• Pending: Received and awaiting verification.\n• Verified: Verified by station manager.\n• Assigned: A field agent has been dispatched to clean it up.\n• Collected: Resolved and cleaned!",
            answerFr = "• En attente : Reçu et en attente de vérification.\n• Vérifié : Confirmé par le responsable de station.\n• Assigné : Un agent de terrain est envoyé sur place.\n• Collecté : Déchets nettoyés avec succès !"
        ),
        FaqItem(
            id = "offline",
            questionEn = "Does EcoLink work offline?",
            questionFr = "EcoLink fonctionne-t-il hors ligne ?",
            answerEn = "Yes! If you have no internet connection, your reports are stored safely on your device and automatically transmitted as soon as connectivity is restored.",
            answerFr = "Oui ! En l'absence de réseau, vos signalements sont enregistrés sur votre téléphone et envoyés automatiquement dès que la connexion revient."
        ),
        FaqItem(
            id = "privacy",
            questionEn = "Is my submission anonymous?",
            questionFr = "Mon signalement est-il anonyme ?",
            answerEn = "Only the photo, location coordinates, and report category are visible to field agents to clean up the site. Your private account details remain secure.",
            answerFr = "Seuls la photo, la géolocalisation et la catégorie sont partagées avec les équipes de nettoyage. Vos données personnelles restent sécurisées."
        )
    )

    fun getQuickQuestions(language: AppLanguage): List<String> {
        return faqList.map { if (language == AppLanguage.FRENCH) it.questionFr else it.questionEn }
    }

    /**
     * Ask Google Gemini AI (with local offline fallback)
     */
    suspend fun queryGemini(query: String, language: AppLanguage): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BACKEND_URL/chat")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.doOutput = true
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            val jsonBody = JSONObject().apply {
                put("message", query)
                put("language", if (language == AppLanguage.FRENCH) "fr" else "en")
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            if (conn.responseCode == 200) {
                val responseStr = BufferedReader(InputStreamReader(conn.inputStream)).readText()
                val jsonRes = JSONObject(responseStr)
                if (jsonRes.optBoolean("success")) {
                    return@withContext jsonRes.optString("answer")
                }
            }
        } catch (e: Exception) {
            // Silence network error and fall back to local rule-based AI
        }

        // Local Fallback
        return@withContext answerUserQueryLocal(query, language)
    }

    fun answerUserQueryLocal(query: String, language: AppLanguage): String {
        val lowerQuery = query.lowercase()

        val match = faqList.firstOrNull { item ->
            lowerQuery.contains("submit") || lowerQuery.contains("signaler") || lowerQuery.contains("send") || lowerQuery.contains("envoyer") && item.id == "submit" ||
            (lowerQuery.contains("status") || lowerQuery.contains("statut") || lowerQuery.contains("assigned") || lowerQuery.contains("assigné") || lowerQuery.contains("review")) && item.id == "status" ||
            (lowerQuery.contains("offline") || lowerQuery.contains("internet") || lowerQuery.contains("connexion") || lowerQuery.contains("réseau")) && item.id == "offline" ||
            (lowerQuery.contains("privacy") || lowerQuery.contains("anonymous") || lowerQuery.contains("anonyme")) && item.id == "privacy"
        }

        if (match != null) {
            return if (language == AppLanguage.FRENCH) match.answerFr else match.answerEn
        }

        return if (language == AppLanguage.FRENCH) {
            "🤖 Assistant EcoLink (Gemini) : Je peux vous aider à comprendre comment créer des signalements, suivre l'état de vos demandes et utiliser le mode hors ligne. Posez-moi une question !"
        } else {
            "🤖 EcoLink Assistant (Gemini): I can help you learn how to report waste, track status updates, and use offline mode. Select a quick question above or ask a question!"
        }
    }

    fun inspectPhotoQuality(isBlurry: Boolean, language: AppLanguage): PhotoNudgeResult {
        return if (isBlurry) {
            if (language == AppLanguage.FRENCH) {
                PhotoNudgeResult(
                    title = "✨ Assistant Qualité Photo EcoLink (Gemini)",
                    message = "Cette photo semble floue ou sombre. Souhaitez-vous la reprendre pour accélérer la validation par la station ?",
                    isQualityGood = false,
                    retakeButtonText = "Reprendre la photo",
                    keepButtonText = "Conserver quand même"
                )
            } else {
                PhotoNudgeResult(
                    title = "✨ EcoLink Photo Inspector (Gemini)",
                    message = "This photo appears blurry or out of focus. Would you like to retake it for faster verification by station managers?",
                    isQualityGood = false,
                    retakeButtonText = "Retake Photo",
                    keepButtonText = "Use Anyway"
                )
            }
        } else {
            if (language == AppLanguage.FRENCH) {
                PhotoNudgeResult(
                    title = "✨ Assistant Qualité Photo EcoLink (Gemini)",
                    message = "Excellente qualité de photo ! Vue claire des déchets détectée.",
                    isQualityGood = true,
                    retakeButtonText = "Changer la photo",
                    keepButtonText = "Parfait"
                )
            } else {
                PhotoNudgeResult(
                    title = "✨ EcoLink Photo Inspector (Gemini)",
                    message = "Photo quality looks clear and sharp! Great capture.",
                    isQualityGood = true,
                    retakeButtonText = "Change Photo",
                    keepButtonText = "Looks Good"
                )
            }
        }
    }
}
