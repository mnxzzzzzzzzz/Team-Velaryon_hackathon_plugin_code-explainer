# 🔑 API Key Setup Guide

## 🚀 Quick Setup Options

### **Option 1: Direct Code Setup (Easiest)**

1. **Open** `src/main/kotlin/com/example/context/OpenAIClient.kt`
2. **Find line 21** with:
   ```kotlin
   private val DIRECT_API_KEY = "your-openai-api-key-here"
   ```
3. **Replace** with your actual API key:
   ```kotlin
   private val DIRECT_API_KEY = "sk-proj-your-actual-api-key-here"
   ```
4. **Rebuild** the plugin: `./gradlew clean build`
5. **Run** the plugin: `./gradlew runIde`

### **Option 2: Environment Variable (More Secure)**

**Windows:**
```cmd
set OPENAI_API_KEY=sk-proj-your-actual-api-key-here
./gradlew runIde
```

**PowerShell:**
```powershell
$env:OPENAI_API_KEY = "sk-proj-your-actual-api-key-here"
./gradlew runIde
```

### **Option 3: Plugin Settings (Runtime)**

1. **Start** the plugin (will be in demo mode initially)
2. **Go to** File → Settings → Code Context Explainer
3. **Enter** your API key
4. **Click** "Test Connection" and "Apply"

## 🔐 Getting Your API Key

1. **Visit** [platform.openai.com/api-keys](https://platform.openai.com/api-keys)
2. **Sign up** or log in to your OpenAI account
3. **Click** "Create new secret key"
4. **Copy** the key (starts with `sk-proj-` or `sk-`)
5. **Use** in any of the setup options above

**💡 New users get $5 free credit!**

## ✅ Verification

After setup, you should see:
- **"AI Mode (OpenAI Connected)"** instead of "Demo Mode"
- **Real AI analysis** when using "Explain with Context"
- **Debug output** showing API key validation (if enabled)

## 🔒 Security Notes

- **Never commit** API keys to version control
- **Use environment variables** for production deployments
- **Regenerate keys** if accidentally exposed
- **Monitor usage** on OpenAI dashboard

## 🐛 Troubleshooting

### **Still showing Demo Mode?**
1. Check API key format (must start with `sk-`)
2. Verify no extra spaces or characters
3. Rebuild plugin after changes: `./gradlew clean build`
4. Check console for debug messages

### **API Errors?**
1. Verify key is valid on OpenAI platform
2. Check account has available credits
3. Ensure internet connection is working
4. Try the "Test Connection" button in settings

### **Build Issues?**
1. Make sure Java 17+ is installed
2. Run `./gradlew clean` then rebuild
3. Check for syntax errors in modified files

---

**🎯 Once configured, your plugin will have full AI-powered code analysis capabilities!**