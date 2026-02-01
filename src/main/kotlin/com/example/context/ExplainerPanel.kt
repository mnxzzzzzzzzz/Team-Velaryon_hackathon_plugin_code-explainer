package com.example.context

import com.intellij.openapi.project.Project
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import javax.swing.*

class ExplainerPanel(private val project: Project) {
    val root = JPanel(BorderLayout())
    private val keyStatusLabel = JLabel()

    init {
        setupUI()
        setupKeyStatus()
    }

    private fun setupUI() {
        val label = JLabel("Select code and use 'Explain with Context' from the menu.")
        label.horizontalAlignment = SwingConstants.CENTER
        root.add(label, BorderLayout.CENTER)
    }

    private fun setupKeyStatus() {
        keyStatusLabel.font = keyStatusLabel.font.deriveFont(11f)
        updateKeyStatus()

        val statusPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            background = Color(0xFFFFFF)
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        }

        val setupButton = JButton("⚙ Setup API Key").apply {
            font = font.deriveFont(10f)
            addActionListener {
                OpenAIClient.quickSetupApiKey(project)
                updateKeyStatus()
            }
        }

        statusPanel.add(keyStatusLabel)
        statusPanel.add(Box.createHorizontalStrut(10))
        statusPanel.add(setupButton)

        // Add to root panel
        root.add(statusPanel, BorderLayout.SOUTH)
    }

    private fun updateKeyStatus() {
        if (OpenAIClient.hasValidKey()) {
            keyStatusLabel.text = "✓ AI Mode (OpenAI Connected)"
            keyStatusLabel.foreground = Color(0, 128, 0)
            keyStatusLabel.toolTipText = "Using real OpenAI API for analysis"
        } else {
            keyStatusLabel.text = "🧠 Smart Analysis Mode"
            keyStatusLabel.foreground = Color(0, 100, 200)
            keyStatusLabel.toolTipText = "Using intelligent pattern-based analysis - setup API key for enhanced AI features"
        }
    }

    companion object {
        // This is a simplified version to match ToolWindowFactory expectation
        fun create(project: Project): JPanel {
            return ExplainerPanel(project).root
        }
    }
}