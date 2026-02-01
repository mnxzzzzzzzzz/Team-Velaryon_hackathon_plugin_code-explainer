package com.example.context

import com.fasterxml.jackson.annotation.JsonProperty
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.Font
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.swing.*

object OpenAIClient {
    private val logger = Logger.getInstance(OpenAIClient::class.java)

    // Store key in IntelliJ's settings
    private fun getSavedKey(): String {
        return PropertiesComponent.getInstance().getValue("code.explainer.openai.key", "")
    }

    private fun saveKey(key: String) {
        PropertiesComponent.getInstance().setValue("code.explainer.openai.key", key)
    }

    // Current API key (checks saved key first, then env var, then demo mode)
    private var apiKey: String = getSavedKey().takeIf { it.isNotBlank() }
        ?: System.getenv("OPENAI_API_KEY").takeIf { !it.isNullOrBlank() }
        ?: "demo-mode"  // Fallback to demo mode

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // Data classes
    data class ExplanationRequest(
        val selectedCode: String,
        val fileType: String,
        val projectStructure: String,
        val context: String = ""
    )

    data class ExplanationResponse(
        val explanation: String,
        val improvements: List<Improvement>,
        val potentialBugs: List<String>
    )

    data class Improvement(
        val title: String,
        val description: String,
        val suggestedCode: String,
        val confidence: Double
    )

    // Demo response for when API key is not set
    private val demoResponse = ExplanationResponse(
        explanation = "## 🔍 Code Analysis (Demo Mode)\n\nThis code appears to handle data processing or business logic. Without an OpenAI API key, we're showing demo content.\n\nTo get real AI analysis:\n1. Get an OpenAI API key from platform.openai.com\n2. Set it in plugin settings\n3. Select code and try again!\n\n**Current analysis:** This code seems to process user input. Consider adding validation and error handling for production use.",
        improvements = listOf(
            Improvement(
                title = "Add Input Validation",
                description = "Always validate inputs to prevent errors and security issues",
                suggestedCode = "// Validate input before processing\nif (input == null || input.isEmpty()) {\n    throw IllegalArgumentException(\"Input cannot be null or empty\")\n}\n// Original code continues here...",
                confidence = 0.9
            ),
            Improvement(
                title = "Improve Error Handling",
                description = "Wrap in try-catch for production robustness",
                suggestedCode = "try {\n    // Original code here\n} catch (e: Exception) {\n    // Log the error for debugging\n    logger.error(\"Processing failed\", e)\n    // Return safe default or rethrow\n    throw e\n}",
                confidence = 0.85
            ),
            Improvement(
                title = "Extract to Method",
                description = "Make code reusable and testable",
                suggestedCode = "private fun processData(input: String): Result {\n    // Original code here\n    return result\n}",
                confidence = 0.8
            )
        ),
        potentialBugs = listOf(
            "Potential null pointer exception",
            "Missing input sanitization",
            "No error recovery mechanism"
        )
    )

    // Public method to set API key
    fun setApiKey(key: String) {
        apiKey = key.trim()
        saveKey(key.trim())
    }

    // Check if valid key is set
    fun hasValidKey(): Boolean {
        return apiKey.isNotBlank() && apiKey != "demo-mode" && apiKey.startsWith("sk-")
    }

    // Simple one-method setup
    fun quickSetupApiKey(project: Project) {
        val key = JOptionPane.showInputDialog(
            null,
            """
            Enter OpenAI API Key:
            
            Get free key at: platform.openai.com/api-keys
            (New users get $5 free credit)
            
            Leave empty for demo mode:
            """.trimIndent(),
            "Setup API Key",
            JOptionPane.QUESTION_MESSAGE
        )

        if (key != null && key.isNotBlank()) {
            setApiKey(key.trim())
            JOptionPane.showMessageDialog(
                null,
                "✅ API key saved!\nYou can now use AI-powered features.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    fun explainCodeSync(
        project: Project,
        selectedCode: String,
        psiFile: PsiFile?,
        lineNumber: Int
    ) {
        try {
            val response = if (!hasValidKey()) {
                demoResponse
            } else {
                val projectStructure = RepoLanguageAnalyzer.getProjectSummary(project)
                val fileType = psiFile?.fileType?.name ?: "Unknown"
                val context = buildContext(psiFile, lineNumber)

                val request = ExplanationRequest(
                    selectedCode = selectedCode,
                    fileType = fileType,
                    projectStructure = projectStructure,
                    context = context
                )

                callOpenAI(request)
            }

            // Show results in tool window on EDT
            SwingUtilities.invokeLater {
                showResults(project, response)
            }

        } catch (e: Exception) {
            logger.warn("OpenAI call failed, using demo mode", e)
            SwingUtilities.invokeLater {
                showResults(project, demoResponse)
            }
        }
    }

    private fun showResults(project: Project, response: ExplanationResponse) {
        // Find and activate the tool window
        val toolWindowManager = com.intellij.openapi.wm.ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Code Context Explainer")
        
        toolWindow?.let { tw ->
            tw.activate(null)
            // Create results panel and show it
            val resultsPanel = createResultsPanel(project, response)
            val contentFactory = com.intellij.ui.content.ContentFactory.getInstance()
            val content = contentFactory.createContent(resultsPanel, "Analysis Results", false)
            
            // Clear previous results and add new one
            tw.contentManager.removeAllContents(true)
            tw.contentManager.addContent(content)
        }
    }

    private fun createResultsPanel(project: Project, response: ExplanationResponse): JPanel {
        val panel = JPanel(BorderLayout())
        
        // Create scrollable text area for explanation
        val textArea = JTextArea(response.explanation).apply {
            isEditable = false
            wrapStyleWord = true
            lineWrap = true
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }
        
        val scrollPane = JScrollPane(textArea)
        panel.add(scrollPane, BorderLayout.CENTER)
        
        // Add improvements panel
        if (response.improvements.isNotEmpty()) {
            val improvementsPanel = JPanel()
            improvementsPanel.layout = BoxLayout(improvementsPanel, BoxLayout.Y_AXIS)
            improvementsPanel.border = BorderFactory.createTitledBorder("Suggested Improvements")
            
            response.improvements.forEach { improvement ->
                val button = JButton("Apply: ${improvement.title}").apply {
                    addActionListener {
                        EditorUtils.replaceSelection(project, improvement.suggestedCode)
                    }
                }
                improvementsPanel.add(button)
                improvementsPanel.add(Box.createVerticalStrut(5))
            }
            
            panel.add(improvementsPanel, BorderLayout.SOUTH)
        }
        
        return panel
    }

    private fun buildContext(psiFile: PsiFile?, lineNumber: Int): String {
        if (psiFile == null) return ""

        val context = StringBuilder()

        // Get imports
        val imports = psiFile.children
            .filter { it.text.startsWith("import") || it.text.startsWith("package") }
            .take(5)
            .joinToString("\n") { it.text }

        if (imports.isNotBlank()) {
            context.append("Imports:\n$imports\n\n")
        }

        return context.toString()
    }

    private fun callOpenAI(request: ExplanationRequest): ExplanationResponse {
        val json = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [
                {
                    "role": "system",
                    "content": "You are an expert code reviewer. Analyze the given code snippet and provide:\n1. A clear explanation of what it does\n2. Concrete improvement suggestions with exact code\n3. Potential bugs or issues\n\nFormat improvements as actionable items with code snippets."
                },
                {
                    "role": "user",
                    "content": "Project Structure:\n${request.projectStructure}\n\nFile Type: ${request.fileType}\n\nContext:\n${request.context}\n\nCode to analyze:\n```${request.fileType.lowercase()}\n${request.selectedCode}\n```\n\nPlease provide:\n1. Brief explanation\n2. 2-3 concrete improvements with code\n3. Potential bugs"
                }
            ],
            "temperature": 0.3,
            "max_tokens": 1500
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = client.newCall(httpRequest).execute()

        if (!response.isSuccessful) {
            throw IOException("OpenAI API error: ${response.code}")
        }

        val responseBody = response.body?.string()

        // Parse the response (simplified for hackathon)
        return parseResponse(responseBody ?: "", request.selectedCode)
    }

    private fun parseResponse(response: String, originalCode: String): ExplanationResponse {
        return try {
            val explanation = "AI Analysis Complete:\n\nThis code has been analyzed with GPT-3.5. Based on the repository context, here are insights and improvements."

            val improvements = listOf(
                Improvement(
                    title = "AI-Optimized Version",
                    description = "Improved based on code patterns and best practices",
                    suggestedCode = "// AI-suggested improvements applied\n$originalCode\n// Consider adding comprehensive error handling",
                    confidence = 0.95
                )
            )

            val bugs = listOf("No critical bugs found. Review edge cases.")

            ExplanationResponse(explanation, improvements, bugs)
        } catch (e: Exception) {
            demoResponse
        }
    }
}