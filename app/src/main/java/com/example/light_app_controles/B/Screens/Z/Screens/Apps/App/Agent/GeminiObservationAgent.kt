package Application5.App.Agent

import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Data.Repo20ObsarvationEtudion
import android.util.Log
import com.example.light_app_controles.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.io.FileWriter

object GeminiObservationAgent {
    private const val TAG = "GeminiObsAgent"

    suspend fun generateAndSaveStructuredJson(
        etudiantKeyId: String,
        studentName: String,
        repo20Observation: Repo20ObsarvationEtudion
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Get student's observations from the database list
                val observations = repo20Observation.datasValue.filter { it.etudiant_keyID == etudiantKeyId }
                if (observations.isEmpty()) {
                    return@withContext "Aucune observation trouvée pour cet étudiant."
                }

                // 2. Prepare prompt with raw observation info
                val observationsRawText = observations.joinToString("\n") { obs ->
                    "ID: ${obs.keyID}, Type: ${obs.type.name}, Tabrire: ${obs.tabrire_riyab}, " +
                            "Du: ${obs.min_soura.name} (Aya ${obs.min_aya}, Satr ${obs.min_sattre}) " +
                            "Au: ${obs.ila_soura.name} (Aya ${obs.ila_aya}, Satr ${obs.ila_sattre}), " +
                            "Evaluation: ${obs.takyim.name}, Remarques: ${obs.moulahadat_takyim_li_islahiha}, " +
                            "Timestamp: ${obs.creationTimestamps}"
                }

                val prompt = """
                    You are an intelligent data parser for a Quranic student management system.
                    Your task is to parse the raw text observations of student "$studentName" (ID: $etudiantKeyId) and output a clean, well-formatted JSON array containing all observation records.
                    
                    For each observation record, you must construct a JSON object matching this structure:
                    {
                      "keyID": "String representing observation ID",
                      "type": "String (one of: Raeeb, Tama_Hifdoha, Moukarrar_Itmamouhou, Ousstad_kama_Bil_moundat)",
                      "tabrire_riyab": "String explanation of absence (if any)",
                      "etudiant_keyID": "String matching $etudiantKeyId",
                      "min_soura": "String matching the starting Soura name",
                      "min_aya": Int (starting Aya number),
                      "min_sattre": Int (starting line/sattre),
                      "ila_soura": "String matching the ending Soura name",
                      "ila_aya": Int (ending Aya number),
                      "ila_sattre": Int (ending line/sattre),
                      "tikrar": Int (repetitions),
                      "el3arde": Int (presentations),
                      "takyim": "String (evaluation/grade name)",
                      "moulahadat": "String (custom feedback remarks)",
                      "creationTimestamps": Long (timestamp in milliseconds)
                    }
                    
                    Input observations:
                    $observationsRawText
                    
                    Output ONLY a valid JSON array, do not include markdown backticks or any introductory text.
                """.trimIndent()

                // 3. Initialize Gemini Model with Structured JSON Output config
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
                    Log.e(TAG, "Gemini API key is blank or placeholder!")
                    return@withContext "Erreur: Clé API Gemini non configurée dans local.properties."
                }

                Log.d(TAG, "Initializing GenerativeModel. Model: gemini-2.0-flash")
                Log.d(TAG, "API Key prefix: ${if (apiKey.length > 6) apiKey.take(6) else "short"}... (Total Length: ${apiKey.length})")

                val model = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = apiKey,
                    generationConfig = generationConfig {
                        responseMimeType = "application/json"
                    }
                )

                // 4. Send request to Gemini API
                Log.d(TAG, "Sending generation request to Gemini API...")
                Log.d(TAG, "Prompt: $prompt")
                
                val response = model.generateContent(prompt)
                val jsonString = response.text ?: return@withContext "L'API Gemini a retourné une réponse vide."

                Log.d(TAG, "Received response from Gemini API: $jsonString")

                // Validate that it is a valid JSON
                JSONArray(jsonString)

                // 5. Save the generated JSON file
                val folder = File("/storage/emulated/0/Abdelwahab_jeMla.com/etudiant_$etudiantKeyId")
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                val file = File(folder, "observations.json")
                FileWriter(file, false).use { it.write(jsonString) }

                Log.d(TAG, "Successfully exported observations to ${file.absolutePath}")
                return@withContext "Succès: Observations enregistrées dans ${file.absolutePath}"
            } catch (e: Exception) {
                Log.e(TAG, "=== GEMINI OBSERVATION AGENT ERROR ===")
                Log.e(TAG, "An exception occurred during Gemini JSON generation or saving.")
                Log.e(TAG, "Exception type: ${e.javaClass.canonicalName}")
                Log.e(TAG, "Exception message: ${e.message}")
                Log.e(TAG, "Stacktrace:", e)
                Log.e(TAG, "💡 Debug Tips for 404/API errors:")
                Log.e(TAG, "  1. Verify if 'geminiApiKey' in local.properties is correct and active.")
                Log.e(TAG, "  2. Confirm your region is supported by the Gemini API endpoint.")
                Log.e(TAG, "  3. Check if the modelName 'gemini-2.0-flash' is still supported or if it's retired.")
                Log.e(TAG, "=======================================")
                return@withContext "Erreur lors de la génération: ${e.localizedMessage}"
            }
        }
    }
}
