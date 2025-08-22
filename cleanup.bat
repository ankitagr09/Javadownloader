@echo off
echo ===== Java Smart Downloader - Cleanup Script =====

REM Clean compiled classes
if exist "classes" (
    echo Removing compiled classes...
    rmdir /s /q classes
)

REM Clean log files
if exist "downloader.log" (
    echo Removing log files...
    del "downloader.log"
)

REM Clean temporary download files
echo Cleaning temporary files...
del "*.part*" 2>nul
del "*.partial" 2>nul

REM Clean IDE files
if exist ".qodo" (
    echo Removing .qodo directory...
    rmdir /s /q ".qodo"
)

echo ✅ Cleanup completed!
echo.
echo To rebuild the project, run: build-all.bat
