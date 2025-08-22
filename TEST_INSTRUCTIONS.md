# Java Smart Downloader - Test Instructions

## Test Both Versions

### 1. Build the project
```cmd
build-all.bat
```

### 2. Test GUI Version (Recommended)
```cmd
run-gui.bat
```

**GUI Test Steps:**
1. **Interface Test**:
   - Verify all UI components load properly
   - Check that buttons are properly styled
   - Confirm progress bar and labels are visible

2. **Basic Download Test**:
   - Enter URL: `https://httpbin.org/bytes/1024` (1KB test file)
   - Click "Browse..." and select destination
   - Set threads to 2
   - Click "▶ Start Download"
   - Verify progress bar updates
   - Check activity log shows timestamps

3. **Error Handling Test**:
   - Try invalid URL: `invalid-url`
   - Try empty URL field
   - Verify error dialogs appear

4. **Pause/Resume Test**:
   - Start download of larger file
   - Click "⏸ Pause" 
   - Click "▶ Resume"
   - Verify download continues

5. **Clear Functionality**:
   - Fill all fields
   - Click "🗑 Clear"
   - Verify all fields reset

### 3. Test Console Version
```cmd
run-console.bat
```

**Console Test Steps:**
1. Choose option 1 (Start new download)
2. Enter test URL: `https://httpbin.org/bytes/1024`
3. Press Enter for current directory
4. Enter 2 for thread count
5. Watch console progress bar
6. Test pause/resume (options 2 & 3)

## Sample Test URLs

### Small Files (for quick testing)
- `https://httpbin.org/bytes/1024` (1KB)
- `https://httpbin.org/bytes/10240` (10KB)

### Medium Files
- `https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf` (Small PDF)

### Large Files (for pause/resume testing)  
- Any GitHub release file (~5-50MB)
- Use with caution based on your internet speed

## Expected Results

### GUI Version ✅
- [x] Modern Swing interface with proper styling
- [x] Real-time progress bar with percentage
- [x] Speed and ETA calculations
- [x] Browse button for directory selection  
- [x] Activity log with timestamps
- [x] Pause/Resume functionality
- [x] Clear button resets all fields
- [x] Error dialogs for invalid inputs
- [x] Color-coded status indicators

### Console Version ✅
- [x] Interactive menu system
- [x] Console progress bar with animation
- [x] Speed and ETA display
- [x] Pause/Resume via menu options
- [x] Status display option
- [x] Proper error handling

## Known Limitations
- Some servers may not support partial downloads (resume won't work)
- Thread count limited to 8 for stability
- GUI version requires Java Swing support
- Basic error recovery (no automatic retry)
- No checksum validation (planned for future)

## Performance Notes
- **1-2 threads**: Good for slow connections
- **4 threads**: Optimal for most broadband
- **6-8 threads**: Only for very fast connections
- More threads ≠ Always faster (depends on server)
