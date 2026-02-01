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

        // Skip the dialog for now to avoid threading issues
        // The user can set API key in Settings if needed

        // Trigger explanation - now handles threading properly
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val lineNumber = editor.caretModel.logicalPosition.line
        
        OpenAIClient.explainCodeSync(project, selection, psiFile, lineNumber)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }
}