@echo off
echo ===== Java Smart Downloader - GUI Version =====

REM Check if classes exist
if not exist "classes\com\downloader\gui\DownloaderSwingGUI.class" (
    echo Classes not found. Building project...
    call build-all.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed! Please check for errors.
        pause
        exit /b 1
    )
)

REM Run the GUI application
echo Starting Java Smart Downloader (GUI)...
echo.
cd classes
java com.downloader.gui.DownloaderSwingGUI
cd ..
