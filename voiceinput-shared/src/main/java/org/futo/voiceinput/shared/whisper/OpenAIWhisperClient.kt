package org.futo.voiceinput.shared.whisper

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.voiceinput.shared.types.Language
import org.futo.voiceinput.shared.types.ModelInferenceCallback
import org.futo.voiceinput.shared.types.InferenceState
import org.futo.voiceinput.shared.types.toWhisperString
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class OpenAIWhisperClient(private val apiKey: String) {
    
    companion object {
        private const val TAG = "OpenAIWhisperClient"
        private const val API_URL = "https://api.openai.com/v1/audio/transcriptions"
    }

    suspend fun transcribe(
        audioData: FloatArray,
        languages: Set<Language>,
        callback: ModelInferenceCallback
    ): String = withContext(Dispatchers.IO) {
        try {
            callback.updateStatus(InferenceState.Encoding)
            
            // Convert float array to WAV format
            val wavData = convertFloatArrayToWav(audioData)
            
            callback.updateStatus(InferenceState.LoadingModel)
            
            // Make API request
            val result = makeAPIRequest(wavData, languages)
            
            return@withContext result
        } catch (e: Exception) {
            Log.e(TAG, "Error during transcription", e)
            throw e
        }
    }
    
    private fun convertFloatArrayToWav(audioData: FloatArray): ByteArray {
        val sampleRate = 16000
        val channels = 1
        val bitsPerSample = 16
        
        val byteArrayOutputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(byteArrayOutputStream)
        
        try {
            // WAV header
            dataOutputStream.writeBytes("RIFF")
            dataOutputStream.writeInt(Integer.reverseBytes(36 + audioData.size * 2))
            dataOutputStream.writeBytes("WAVE")
            dataOutputStream.writeBytes("fmt ")
            dataOutputStream.writeInt(Integer.reverseBytes(16)) // Sub-chunk size
            dataOutputStream.writeShort(Short.reverseBytes(1)) // Audio format (PCM)
            dataOutputStream.writeShort(Short.reverseBytes(channels.toShort())) // Number of channels
            dataOutputStream.writeInt(Integer.reverseBytes(sampleRate)) // Sample rate
            dataOutputStream.writeInt(Integer.reverseBytes(sampleRate * channels * bitsPerSample / 8)) // Byte rate
            dataOutputStream.writeShort(Short.reverseBytes((channels * bitsPerSample / 8).toShort())) // Block align
            dataOutputStream.writeShort(Short.reverseBytes(bitsPerSample.toShort())) // Bits per sample
            dataOutputStream.writeBytes("data")
            dataOutputStream.writeInt(Integer.reverseBytes(audioData.size * 2)) // Data size
            
            // Convert float samples to 16-bit PCM
            for (sample in audioData) {
                val intSample = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                dataOutputStream.writeShort(Short.reverseBytes(intSample.toShort()))
            }
            
            dataOutputStream.close()
            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException("Error creating WAV data", e)
        }
    }
    
    private fun makeAPIRequest(wavData: ByteArray, languages: Set<Language>): String {
        val url = URL(API_URL)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
            connection.doOutput = true
            
            val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
            val outputStream = connection.outputStream
            val writer = DataOutputStream(outputStream)
            
            // Write file part
            writer.writeBytes("--$boundary\r\n")
            writer.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"\r\n")
            writer.writeBytes("Content-Type: audio/wav\r\n\r\n")
            writer.write(wavData)
            writer.writeBytes("\r\n")
            
            // Write model part
            writer.writeBytes("--$boundary\r\n")
            writer.writeBytes("Content-Disposition: form-data; name=\"model\"\r\n\r\n")
            writer.writeBytes("whisper-1\r\n")
            
            // Write language part if specified
            if (languages.isNotEmpty()) {
                val language = languages.first().toWhisperString()
                if (language.isNotEmpty()) {
                    writer.writeBytes("--$boundary\r\n")
                    writer.writeBytes("Content-Disposition: form-data; name=\"language\"\r\n\r\n")
                    writer.writeBytes("$language\r\n")
                }
            }
            
            writer.writeBytes("--$boundary--\r\n")
            writer.close()
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                
                val jsonResponse = JSONObject(response)
                return jsonResponse.getString("text")
            } else {
                val errorStream = connection.errorStream
                val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                throw RuntimeException("API request failed with code $responseCode: $errorResponse")
            }
        } finally {
            connection.disconnect()
        }
    }
}