# Code Context Explainer - IntelliJ IDEA Plugin

AI-powered code explanations with full repository context. Select code, right-click "Explain with Context" to get intelligent analysis and improvements.

## 🚀 Quick Start

### Prerequisites
- **Java 17 or later** (Required for building)
- IntelliJ IDEA 2023.3 or later

### Install Java (if not installed)
```cmd
# Option 1: Using Chocolatey (Recommended)
choco install openjdk17

# Option 2: Using Scoop
scoop bucket add java
scoop install openjdk17

# Option 3: Download from https://adoptium.net/temurin/releases/
```

### Build & Run
```cmd
# Quick build (Windows)
build.bat

# Or manually:
./gradlew clean build buildPlugin

# Test in development IDE
dev.bat
# Or manually:
./gradlew runIde
```

## 📦 Installation

1. Build the plugin (see above)
2. Open IntelliJ IDEA
3. Go to **File → Settings → Plugins**
4. Click **⚙️ → Install Plugin from Disk**
5. Select `build/distributions/code-context-explainer-1.0.0.zip`
6. Restart IntelliJ IDEA

## 🎯 Features

### Demo Mode (No API Key Required)
- ✅ Smart code analysis
- ✅ Sample improvements with real code suggestions
- ✅ One-click code replacement
- ✅ Repository context analysis
- ✅ Perfect for testing the plugin

### AI Mode (OpenAI API Key)
- 🤖 Real GPT-3.5 analysis
- 🎯 Context-aware explanations
- 🔧 Intelligent improvements
- 🐛 Bug detection

## 🔧 Usage

1. **Select any code** in the editor
2. **Right-click** → "Explain with Context"
3. **View results** in the "Code Context Explainer" tool window (right panel)
4. **Apply improvements** with one click
5. **Setup API key** (optional) in Settings → Code Context Explainer

## ⚙️ Configuration

### OpenAI API Key (Optional)
1. Go to **File → Settings → Code Context Explainer**
2. Get free API key from [platform.openai.com/api-keys](https://platform.openai.com/api-keys)
3. Enter key and click "Test Connection"
4. Click "Apply"

*New OpenAI users get $5 free credit!*

## 🛠️ Development

### Project Structure
```
src/main/kotlin/com/example/context/
├── ExplainAction.kt           # Right-click menu action
├── OpenAIClient.kt            # AI integration & demo mode
├── ExplainerPanel.kt          # Tool window UI
├── ExplainerToolWindowFactory.kt # Tool window setup
├── RepoLanguageAnalyzer.kt    # Project analysis
├── SettingsConfigurable.kt    # Settings UI
└── EditorUtils.kt             # Code replacement utilities
```

### Build Commands
```cmd
./gradlew clean                # Clean build
./gradlew build               # Compile and test
./gradlew buildPlugin         # Create distribution ZIP
./gradlew runIde             # Test in development IDE
./gradlew publishPlugin      # Publish to JetBrains Marketplace
```

### Debugging
- Use `./gradlew runIde` to test in a sandboxed IntelliJ instance
- Check `build/idea-sandbox/system/log/idea.log` for errors
- Use IntelliJ's "Plugin DevKit" for advanced debugging

## 🎨 How It Works

1. **Context Gathering**: Analyzes project structure and file relationships
2. **Code Analysis**: Processes selected code with repository context
3. **AI Processing**: Sends to OpenAI (or uses demo analysis)
4. **Smart Suggestions**: Returns actionable improvements with exact code
5. **One-Click Apply**: Replace code directly in editor

## 🔍 Example Output

```
📊 Project Analysis:
Total files: 156
Top languages:
• Kotlin: 45 files (29%)
• Java: 32 files (21%)
• XML: 28 files (18%)

🔍 Code Analysis:
This function handles user authentication...

💡 Suggested Improvements:
[Apply: Add Input Validation] - Click to replace code
[Apply: Improve Error Handling] - Click to replace code
[Apply: Extract to Method] - Click to replace code
```

## 🚨 Troubleshooting

### Build Issues
- **"JAVA_HOME not set"**: Install Java 17+ and set JAVA_HOME
- **"Gradle build failed"**: Run `./gradlew clean` then `./gradlew build`
- **"Plugin not loading"**: Check IntelliJ version compatibility (2023.3+)

### Runtime Issues
- **"Action not visible"**: Select code first, then right-click
- **"Tool window empty"**: Try the action again, check for errors in IDE log
- **"API key not working"**: Verify key starts with 'sk-' and test connection

## 📄 License

MIT License - Feel free to modify and distribute.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Test with `./gradlew runIde`
5. Submit pull request

---

**Made with ❤️ for developers who want smarter code analysis**