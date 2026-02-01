# Setup Instructions for Code Context Explainer Plugin

## Prerequisites

### 1. Install Java 17 or later
**Option A: Using Chocolatey (Recommended for Windows)**
```cmd
# Install Chocolatey if not already installed
# Run as Administrator in PowerShell:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install OpenJDK 17
choco install openjdk17
```

**Option B: Manual Installation**
1. Download OpenJDK 17 from: https://adoptium.net/temurin/releases/
2. Install and add to PATH
3. Set JAVA_HOME environment variable

**Option C: Using Scoop**
```cmd
# Install Scoop if not already installed
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# Install Java
scoop bucket add java
scoop install openjdk17
```

### 2. Verify Installation
```cmd
java -version
javac -version
```

## Building the Plugin

### 1. Clean and Build
```cmd
./gradlew clean build
```

### 2. Run in Development
```cmd
./gradlew runIde
```

### 3. Build Distribution
```cmd
./gradlew buildPlugin
```

The plugin ZIP will be in `build/distributions/`

## Installation in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Go to File → Settings → Plugins
3. Click gear icon → Install Plugin from Disk
4. Select the built ZIP file
5. Restart IntelliJ IDEA

## Usage

1. Select any code in the editor
2. Right-click → "Explain with Context"
3. View results in the "Code Context Explainer" tool window (right panel)
4. Optionally set up OpenAI API key in Settings for AI features

## Features

- **Demo Mode**: Works without API key with sample analysis
- **AI Mode**: Real OpenAI analysis with API key
- **Repository Context**: Analyzes project structure
- **Code Improvements**: Suggests concrete improvements
- **One-click Apply**: Replace code with improvements