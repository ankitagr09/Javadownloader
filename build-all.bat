@echo off
echo ===== Java Smart Downloader - Build Script =====

REM Clean previous builds
if exist "classes" rmdir /s /q classes
mkdir classes

REM Compile Java files
echo Compiling Java files...
javac -d classes src\com\downloader\Utils.java src\com\downloader\Downloader.java src\com\downloader\Main.java src\com\downloader\gui\DownloaderSwingGUI.java

if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful!
    echo.
    echo Available versions:
    echo 1. Console version: java com.downloader.Main
    echo 2. GUI version: java com.downloader.gui.DownloaderSwingGUI
    echo.
    echo Or use the run scripts:
    echo - run-console.bat (for console version)
    echo - run-gui.bat (for GUI version)
) else (
    echo ❌ Compilation failed!
    pause
)
