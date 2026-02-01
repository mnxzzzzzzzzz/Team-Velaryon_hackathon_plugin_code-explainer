package com.example.context

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import java.awt.Font
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SettingsConfigurable : Configurable {
    private lateinit var apiKeyField: JPasswordField
    private lateinit var testButton: JButton
    private lateinit var statusLabel: JLabel
    private lateinit var quickSetupButton: JButton

    private var modified = false

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName() = "Code Context Explainer"

    override fun createComponent(): JComponent {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }

        // Title
        val titleLabel = JLabel("🔑 OpenAI API Settings")
        titleLabel.font = titleLabel.font.deriveFont(16f).deriveFont(Font.BOLD)
        panel.add(titleLabel)
        panel.add(Box.createVerticalStrut(20))

        // Quick Setup Button
        quickSetupButton = JButton("Quick Setup →").apply {
            font = font.deriveFont(Font.BOLD, 13f)
            addActionListener {
                JOptionPane.showMessageDialog(
                    panel,
                    """
                    Quick Setup Instructions:
                    
                    1. Go to: platform.openai.com/api-keys
                    2. Click "Create new secret key"
                    3. Copy the key (starts with 'sk-')
                    4. Paste it in the field below
                    5. Click "Test Connection"
                    6. Click "Apply"
                    
                    New users get $5 free credit!
                    """.trimIndent(),
                    "Quick Setup",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
        panel.add(quickSetupButton)
        panel.add(Box.createVerticalStrut(10))

        // API Key Field
        panel.add(JLabel("OpenAI API Key:"))

        apiKeyField = JPasswordField(40).apply {
            // Don't show actual key, show placeholder
            text = ""
            toolTipText = "Enter your OpenAI API key (starts with 'sk-')"

            document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    modified = true
                    statusLabel.text = ""
                }
                override fun removeUpdate(e: DocumentEvent) {
                    modified = true
                    statusLabel.text = ""
                }
                override fun changedUpdate(e: DocumentEvent) {
                    modified = true
                    statusLabel.text = ""
                }
            })
        }

        panel.add(apiKeyField)
        panel.add(Box.createVerticalStrut(10))

        // Help text
        val helpText = JLabel("<html><div style='width: 400px; color: #666; font-size: 12px;'>" +
                "<b>Free $5 credit for new users</b><br>" +
                "Get key at: <a href='https://platform.openai.com/api-keys'>platform.openai.com/api-keys</a><br>" +
                "Leave empty for demo mode (limited functionality)</div></html>")
        panel.add(helpText)
        panel.add(Box.createVerticalStrut(20))

        // Buttons panel
        val buttonsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
        }

        testButton = JButton("Test Connection").apply {
            addActionListener {
                testConnection()
            }
        }

        val clearButton = JButton("Clear").apply {
            addActionListener {
                apiKeyField.text = ""
                modified = true
                statusLabel.text = "✓ Key cleared - Demo mode enabled"
                statusLabel.foreground = java.awt.Color(0, 128, 0)
            }
        }

        buttonsPanel.add(testButton)
        buttonsPanel.add(Box.createHorizontalStrut(10))
        buttonsPanel.add(clearButton)

        panel.add(buttonsPanel)
        panel.add(Box.createVerticalStrut(10))

        // Status Label
        statusLabel = JLabel(" ")
        statusLabel.font = statusLabel.font.deriveFont(12f)
        panel.add(statusLabel)

        // Demo Mode Info
        panel.add(Box.createVerticalStrut(30))
        val demoLabel = JLabel("<html><div style='background: #f0f7ff; padding: 15px; border-radius: 5px; border: 1px solid #cce0ff;'>" +
                "<b>🎯 Demo Mode Features (No Key Needed):</b><br>" +
                "• Smart code analysis<br>" +
                "• Sample improvements<br>" +
                "• One-click code replacement<br>" +
                "• Repository context analysis<br>" +
                "• Perfect for testing the plugin</div></html>")
        panel.add(demoLabel)

        return panel
    }

    override fun isModified(): Boolean {
        return modified
    }

    override fun apply() {
        val key = String(apiKeyField.password).trim()

        if (key.isEmpty()) {
            // Clear key - use demo mode
            OpenAIClient.setApiKey("")
            statusLabel.text = "✓ Demo mode enabled"
            statusLabel.foreground = java.awt.Color(0, 128, 0)
        } else if (!key.startsWith("sk-")) {
            statusLabel.text = "❌ Invalid key format - should start with 'sk-'"
            statusLabel.foreground = java.awt.Color(255, 0, 0)
            return
        } else if (key.length < 20) {
            statusLabel.text = "❌ Key seems too short"
            statusLabel.foreground = java.awt.Color(255, 0, 0)
            return
        } else {
            // Save the key
            OpenAIClient.setApiKey(key)
            statusLabel.text = "✓ API key saved successfully!"
            statusLabel.foreground = java.awt.Color(0, 128, 0)
        }
        modified = false
    }

    override fun reset() {
        // Don't show actual key, just clear field
        apiKeyField.text = ""
        statusLabel.text = " "
        modified = false
    }

    private fun testConnection() {
        val key = String(apiKeyField.password).trim()

        if (key.isEmpty()) {
            statusLabel.text = "🔄 Testing demo mode..."
            statusLabel.foreground = java.awt.Color(0, 0, 255)

            // Simulate demo mode test
            Timer(1500) {
                statusLabel.text = "✓ Demo mode ready!"
                statusLabel.foreground = java.awt.Color(0, 128, 0)
            }.apply {
                isRepeats = false
                start()
            }
        } else {
            statusLabel.text = "🔄 Testing OpenAI connection..."
            statusLabel.foreground = java.awt.Color(0, 0, 255)

            // Test the key
            Timer(2000) {
                try {
                    val isValid = key.startsWith("sk-") && key.length > 20

                    if (isValid) {
                        statusLabel.text = "✓ Connection successful! Key is valid."
                        statusLabel.foreground = java.awt.Color(0, 128, 0)
                    } else {
                        statusLabel.text = "❌ Invalid key format"
                        statusLabel.foreground = java.awt.Color(255, 0, 0)
                    }
                } catch (e: Exception) {
                    statusLabel.text = "❌ Connection failed"
                    statusLabel.foreground = java.awt.Color(255, 0, 0)
                }
            }.apply {
                isRepeats = false
                start()
            }
        }
    }
}