@echo off
echo Building Code Context Explainer Plugin...
echo.

echo Step 1: Cleaning previous build...
call gradlew clean

echo.
echo Step 2: Building plugin...
call gradlew build

echo.
echo Step 3: Building distribution...
call gradlew buildPlugin

echo.
echo Build complete! Plugin ZIP is in build/distributions/
echo.
echo To test in development IDE:
echo   gradlew runIde
echo.
pause