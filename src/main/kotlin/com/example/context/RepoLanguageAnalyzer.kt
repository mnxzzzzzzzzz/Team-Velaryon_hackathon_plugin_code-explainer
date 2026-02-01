package com.example.context

import com.intellij.openapi.project.Project
import java.io.File

object RepoLanguageAnalyzer {

    fun analyze(project: Project): Map<String, Int> {
        val basePath = project.basePath ?: return emptyMap()
        val counts = mutableMapOf<String, Int>()

        File(basePath).walkTopDown()
            .maxDepth(3) // Limit depth for performance
            .filter { it.isFile }
            .forEach { file ->
                val extension = file.extension.takeIf { it.isNotBlank() } ?: "no_extension"
                val language = mapExtensionToLanguage(extension)
                counts[language] = counts.getOrDefault(language, 0) + 1
            }

        return counts.filter { it.value > 0 }
    }

    private fun mapExtensionToLanguage(extension: String): String {
        return when (extension.lowercase()) {
            "kt", "kts" -> "Kotlin"
            "java" -> "Java"
            "py" -> "Python"
            "js" -> "JavaScript"
            "ts", "tsx" -> "TypeScript"
            "go" -> "Go"
            "rs" -> "Rust"
            "cpp", "cc", "cxx", "hpp", "h" -> "C++"
            "c" -> "C"
            "swift" -> "Swift"
            "rb" -> "Ruby"
            "php" -> "PHP"
            "cs" -> "C#"
            "scala" -> "Scala"
            "groovy" -> "Groovy"
            "gradle", "gradle.kts" -> "Gradle"
            "xml", "json", "yaml", "yml", "properties" -> "Config"
            "md", "txt", "rst" -> "Documentation"
            else -> extension
        }
    }

    fun getProjectSummary(project: Project): String {
        val analysis = analyze(project)

        if (analysis.isEmpty()) {
            return "No files found to analyze"
        }

        val totalFiles = analysis.values.sum()
        val topLanguages = analysis.entries
            .sortedByDescending { it.value }
            .take(5)

        val summary = StringBuilder()
        summary.append("📊 Project Analysis:\n")
        summary.append("Total files: $totalFiles\n\n")
        summary.append("Top languages:\n")

        topLanguages.forEach { (language, count) ->
            val percentage = (count * 100) / totalFiles
            summary.append("• $language: $count files (${percentage}%)\n")
        }

        return summary.toString()
    }
}