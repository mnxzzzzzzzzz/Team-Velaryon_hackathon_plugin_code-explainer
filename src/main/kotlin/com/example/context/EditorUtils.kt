package com.example.context

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.StatusBar

object EditorUtils {

    fun replaceSelection(project: Project, newText: String) {
        val editors = EditorFactory.getInstance().allEditors

        if (editors.isEmpty()) return

        val editor = editors.first()
        val selectionModel = editor.selectionModel

        if (!selectionModel.hasSelection()) {
            // If no selection, insert at cursor
            val offset = editor.caretModel.offset
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.insertString(offset, "\n$newText")
            }
        } else {
            // Replace selection
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(
                    selectionModel.selectionStart,
                    selectionModel.selectionEnd,
                    newText
                )
            }
        }

        // Show notification
        showNotification(project, "Code improvement applied! Use Ctrl+Z to undo.")
    }

    private fun showNotification(project: Project, message: String) {
        try {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            statusBar?.info = message
        } catch (e: Exception) {
            // Fallback - just log if status bar access fails
            println("Notification: $message")
        }
    }
}