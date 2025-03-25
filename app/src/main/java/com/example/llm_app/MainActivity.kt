package com.example.llm_app

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.llm_app.ui.theme.LlmappTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

//    private lateinit var etPrompt: EditText
//    private lateinit var tvResponse: TextView
//    private lateinit var btnSend: Button
//    private lateinit var btnCancel: Button
//    private lateinit var rbOpenAI: RadioButton
//    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
//
//    private val openAIKey = "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"
//    private val coroutineScope = CoroutineScope(Dispatchers.Main)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        etPrompt = findViewById(R.id.etPrompt)
//        tvResponse = findViewById(R.id.tvResponse)
//        btnSend = findViewById(R.id.btnSend)
//        btnCancel = findViewById(R.id.btnCancel)
//        rbOpenAI = findViewById(R.id.rbOpenAI)
//
//        btnSend.setOnClickListener {
//            val prompt = etPrompt.text.toString()
//            if (prompt.isNotEmpty()) {
//                callOpenAI(prompt)
//            } else {
//                Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        btnCancel.setOnClickListener {
//            etPrompt.text.clear()
//            tvResponse.text = ""
//        }
//    }
//
//    private fun callOpenAI(prompt: String) {
//        coroutineScope.launch {
//            val response = withContext(Dispatchers.IO) {
//                sendPromptToOpenAI(prompt)
//            }
//            tvResponse.text = response
//        }
//    }
//
//    private fun sendPromptToOpenAI(prompt: String): String {
//        val apiUrl = "https://api.openai.com/v1/chat/completions"
//
//        return try {
//            val url = URL(apiUrl)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.requestMethod = "POST"
//            connection.setRequestProperty("Content-Type", "application/json")
//            connection.setRequestProperty("Authorization", "Bearer $openAIKey")
//            connection.doOutput = true
//
//            // Construct JSON body
//            val requestBody = JSONObject().apply {
//                put("model", "gpt-3.5-turbo")
//                put("messages", listOf(
//                    mapOf("role" to "user", "content" to prompt)
//                ))
//            }
//
//            // Send request
//            val outputStream = DataOutputStream(connection.outputStream)
//            outputStream.writeBytes(requestBody.toString())
//            outputStream.flush()
//            outputStream.close()
//
//            // Read the response code
//            val responseCode = connection.responseCode
//            val inputStream: InputStream = if (responseCode in 200..299) {
//                connection.inputStream
//            } else {
//                connection.errorStream // get error body
//            }
//
//            val responseText = inputStream.bufferedReader().readText()
//
//            // Log raw response
//            Log.d("OPENAI_RESPONSE", responseText)
//
//            if (responseCode !in 200..299) {
//                return "API Error ($responseCode): $responseText"
//            }
//
//            // Parse the successful response
//            val json = JSONObject(responseText)
//            val reply = json.getJSONArray("choices")
//                .getJSONObject(0)
//                .getJSONObject("message")
//                .getString("content")
//
//            return reply.trim()
//
//        } catch (e: Exception) {
//            return "Exception: ${e.message}"
//        }
//    }
private lateinit var etPrompt: EditText
        private lateinit var tvResponse: TextView
    private lateinit var btnSend: Button
    private lateinit var btnCancel: Button
    private lateinit var rbOpenAI: RadioButton
    private val huggingFaceToken = "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"
    private val mistralUrl = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI elements
        etPrompt = findViewById(R.id.etPrompt)
        tvResponse = findViewById(R.id.tvResponse)
        btnSend = findViewById(R.id.btnSend)
        btnCancel = findViewById(R.id.btnCancel)
//        private val coroutineScope = CoroutineScope(Dispatchers.Main)

        // Handle Send button
        btnSend.setOnClickListener {
            val prompt = etPrompt.text.toString()
            if (prompt.isNotEmpty()) {
                callMistral(prompt)
            } else {
                Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Cancel button
        btnCancel.setOnClickListener {
            etPrompt.text.clear()
            tvResponse.text = ""
        }
    }

    private fun callMistral(prompt: String) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val response = withContext(Dispatchers.IO) {
                sendPromptToMistral(prompt)
            }
            tvResponse.text = response
        }
    }

    private fun sendPromptToMistral(prompt: String): String {
        return try {
            val url = URL(mistralUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $huggingFaceToken")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val requestBody = JSONObject().apply {
                put("inputs", "<s>[INST] $prompt [/INST]")
            }

            val output = DataOutputStream(connection.outputStream)
            output.writeBytes(requestBody.toString())
            output.flush()
            output.close()

            val responseCode = connection.responseCode
            val inputStream: InputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = inputStream.bufferedReader().readText()
            Log.d("MISTRAL_RESPONSE", responseText)

            if (responseCode !in 200..299) {
                return "Mistral API Error ($responseCode): $responseText"
            }

            val jsonArray = JSONArray(responseText)
            val content = jsonArray.getJSONObject(0).getString("generated_text")

            return content.replace(Regex("<.*?>"), "").trim()

        } catch (e: Exception) {
            return "Exception: ${e.message}"
        }
    }
}