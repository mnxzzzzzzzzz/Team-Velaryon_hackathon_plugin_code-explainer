package com.example.context

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import javax.swing.JOptionPane

class ExplainAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)

        val selection = editor.selectionModel.selectedText
        if (selection.isNullOrEmpty()) {
            return
        }

        // Optional: Prompt for API key on first use
        if (!OpenAIClient.hasValidKey()) {
            val option = JOptionPane.showConfirmDialog(
                null,
                "Using demo mode. Want to setup OpenAI API key for AI features?\n\n(Free $5 credit for new users)",
                "Setup API Key",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            )

            if (option == JOptionPane.YES_OPTION) {
                OpenAIClient.quickSetupApiKey(project)
            }
        }

        // Trigger explanation
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val lineNumber = editor.caretModel.logicalPosition.line
        
        // Run in background thread
        Thread {
            try {
                OpenAIClient.explainCodeSync(project, selection, psiFile, lineNumber)
            } catch (e: Exception) {
                // Handle error
                println("Error explaining code: ${e.message}")
            }
        }.start()
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }
}