
## **📄 README.md (MVP Version)**

```markdown
# Java Smart Downloader (MVP - 2025)

A lightweight **multi-threaded Java file downloader** built for learning and rapid testing.  
Supports **parallel downloads, resume support, progress tracking**, and **basic console interface**.

---

## 🚀 Features (MVP)
- ✅ Download files via **direct URL** (HTTP/HTTPS)
- ✅ **Multi-threaded download** for faster speed (1-8 threads)
- ✅ **Pause and Resume** functionality (using partial file save)
- ✅ **Download queue** management
- ✅ **Progress bar** in console with real-time stats
- ✅ Error handling and retry on failure
- ✅ **File validation** and cleanup
- ✅ **Speed calculation** and ETA display
- ✅ **Disk space checking** before download
- ✅ **Logging system** for debugging

### Future Features (Post-MVP)
- 🔒 **Checksum validation** (MD5/SHA)
- 🔁 Auto-resume after crash
- 📁 Folder management (auto-create directories)
- 🌐 Proxy support
- 📦 GUI using JavaFX or Swing
- 🔊 Sound notification on completion

---

## 🛠️ Tech Stack
- **Language**: Java 17+  
- **Core APIs**: `HttpURLConnection`, `BufferedInputStream`, `FileOutputStream`
- **Threading**: `ExecutorService`, `Future`, `AtomicLong`
- **File I/O**: Standard Java I/O with multi-part file handling

---

## 📂 Project Structure
```
File Downloader/
├── src/com/downloader/
│   ├── Main.java                    # Console interface and menu system
│   ├── Downloader.java             # Core download logic with multi-threading
│   ├── Utils.java                  # Helper utilities and file operations
│   └── gui/
│       └── DownloaderSwingGUI.java  # Swing-based GUI interface
├── build-all.bat                   # Compilation script (both versions)
├── run-console.bat                 # Run console version
├── run-gui.bat                     # Run GUI version  
├── cleanup.bat                     # Clean temporary files and rebuild
├── TEST_INSTRUCTIONS.md            # Testing guidelines
├── .gitignore                      # Git ignore rules
└── Readme.md                       # This file
```
```
File Downloader/
├── src/com/downloader/
│   ├── Main.java                    # Console interface and menu system
│   ├── Downloader.java             # Core download logic with multi-threading
│   ├── Utils.java                  # Helper utilities and file operations
│   └── gui/
│       └── DownloaderSwingGUI.java  # Swing-based GUI interface
├── build-all.bat                   # Compilation script for both versions
├── run-console.bat                 # Run console version
├── run-gui.bat                     # Run GUI version
├── TEST_INSTRUCTIONS.md            # Testing guidelines
├── .gitignore                      # Git ignore file
└── Readme.md                       # This file
```

---

## � Quick Start

### Prerequisites
- Java 17 or higher installed
- Internet connection for downloads

### Build and Run
1. **Clone or download** this project
2. **Build both versions**:
   ```cmd
   build-all.bat
   ```
3. **Run your preferred version**:
   - **GUI Version** (Recommended):
     ```cmd
     run-gui.bat
     ```
   - **Console Version**:
     ```cmd
     run-console.bat
     ```

### Cleanup (Optional)
To clean temporary files and rebuild:
```cmd
cleanup.bat
```

### Manual Compilation (Alternative)
```cmd
# Compile all files
javac -d classes src\com\downloader\*.java src\com\downloader\gui\*.java

# Run GUI version
cd classes
java com.downloader.gui.DownloaderSwingGUI

# OR run console version
java com.downloader.Main
```

---

## 📖 Usage

### GUI Version (Recommended)
The Swing-based GUI provides an intuitive interface with:
- **URL input field** with validation
- **Browse button** for destination selection
- **Thread count spinner** (1-8 threads)
- **Real-time progress bar** with percentage
- **Speed and ETA display**
- **Control buttons**: Start, Pause, Resume, Clear
- **Activity log** with timestamps
- **Status indicators** with color coding

**Features:**
- Drag & drop friendly interface
- Visual progress tracking
- One-click directory selection
- Error dialogs with clear messages
- Automatic UI state management

### Console Version
Traditional command-line interface with menu options:

```
=== Java Smart Downloader (MVP) ===
Multi-threaded file downloader with pause/resume support

Choose an option:
1. Start new download    - Begin downloading a file from URL
2. Pause current download - Pause active download (saves progress)
3. Resume download       - Resume paused download
4. Show download status  - Display current download information
5. Exit                 - Quit the application
```

### Example Download Session (GUI)
1. Launch GUI: `run-gui.bat`
2. Enter URL: `https://example.com/file.zip`
3. Click "Browse..." to select destination
4. Set threads (2-4 recommended)
5. Click "▶ Start Download"
6. Watch real-time progress with speed/ETA
7. Use "⏸ Pause" / "▶ Resume" as needed

### Example Download Session (Console)
```
=== Java Smart Downloader (MVP) ===
Choose an option: 1

Enter the URL to download: https://example.com/file.zip
Enter destination directory: C:\Downloads
Enter number of threads (1-8, default 4): 4

Starting download:
URL: https://example.com/file.zip
Destination: C:\Downloads\file.zip
Threads: 4
----------------------------------------

File size: 50.2 MB
[██████████████░░░░░░░░░░░░░░░░] 45.2% | 22.7 MB/50.2 MB | Speed: 2.1 MB/s | ETA: 13s
```

---

## 🔧 Features Explained

### Multi-threaded Downloads
- Automatically splits large files into chunks
- Each thread downloads a separate part simultaneously
- Configurable thread count (1-8 threads)
- Falls back to single-thread for servers that don't support range requests

### Pause/Resume Support
- Downloads can be paused at any time
- Progress is saved to `.part` files
- Resume from exact byte position
- Automatic cleanup of temporary files

### Progress Tracking
- Real-time progress bar with percentage
- Download speed calculation
- ETA (Estimated Time of Arrival)
- Total time and average speed on completion

### Error Handling
- Connection timeout handling
- Automatic retry on network errors
- Server error code detection
- Comprehensive logging to `downloader.log`

### File Management
- Automatic filename extraction from URL
- Windows-compatible filename sanitization
- Duplicate handling with timestamps
- Disk space verification before download

---

## � Troubleshooting

### Common Issues

**"Server returned error code: 403/404"**
- Check if the URL is correct and accessible
- Some servers block direct downloads
- Try downloading with a browser first

**"Download speed is slow"**
- Reduce number of threads (some servers limit concurrent connections)
- Check your internet connection
- Server might have bandwidth limitations

**"Can't resume download"**
- Server might not support partial downloads
- Try starting a fresh download
- Check if `.part` files exist in destination

**"Out of disk space"**
- Free up space in destination directory
- Choose different destination
- Check available space before large downloads

### Log Files
Check `downloader.log` for detailed error information and debugging data.

---

## 🔧 Configuration

### Default Settings
- **Threads**: 4 (adjustable 1-8)
- **Buffer Size**: 8KB per thread
- **Connection Timeout**: 10 seconds
- **Read Timeout**: 30 seconds
- **Progress Update**: Every 1 second

### Customization
Modify the constants in `Downloader.java` to adjust:
- Buffer sizes
- Timeout values
- Progress update frequency
- Thread limits

---

## 📝 How to Contribute
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Create a pull request

Suggestions for contributions:
- GUI implementation (JavaFX/Swing)
- Checksum validation
- Download queue management
- Bandwidth limiting
- Proxy support

---

## 📜 License

MIT License © 2025

---

## 🎯 Learning Objectives

This project demonstrates:
- **Multi-threading** in Java
- **File I/O** operations
- **Network programming** with HTTP
- **Concurrency control** with atomic operations
- **Error handling** and recovery
- **Progress tracking** and user feedback
- **Clean code architecture** with separation of concerns

Perfect for learning Java concurrency, networking, and building practical applications!
```
