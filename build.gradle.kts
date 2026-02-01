plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

group = "com.example.context"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
}

intellij {
    version.set("2023.3.6")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    
    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("241.*")
        version.set(project.version.toString())
        changeNotes.set("""
            <h3>Version 1.0.0</h3>
            <ul>
                <li>AI-powered code explanations with repository context</li>
                <li>Demo mode for testing without API key</li>
                <li>One-click code improvements</li>
                <li>Project structure analysis</li>
            </ul>
        """.trimIndent())
    }
    
    buildSearchableOptions {
        enabled = false
    }
}