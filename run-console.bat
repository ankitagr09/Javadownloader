@echo off
echo ===== Java Smart Downloader - Console Version =====

REM Check if classes exist
if not exist "classes\com\downloader\Main.class" (
    echo Classes not found. Building project...
    call build-all.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed! Please check for errors.
        pause
        exit /b 1
    )
)

REM Run the console application
echo Starting Java Smart Downloader (Console)...
echo.
cd classes
java com.downloader.Main
cd ..
