package com.downloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for common operations and helper methods
 */
public class Utils {
    private static final String LOG_FILE = "downloader.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Extract filename from URL
     */
    public static String extractFileName(String urlString) {
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            String path = url.getPath();
            
            // Get the last part of the path
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            // If no filename found, generate one
            if (fileName.isEmpty() || !fileName.contains(".")) {
                fileName = "download_" + System.currentTimeMillis();
                
                // Try to guess extension from content type or URL
                String extension = guessFileExtension(urlString);
                if (!extension.isEmpty()) {
                    fileName += "." + extension;
                }
            }
            
            // Remove any query parameters or fragments
            int queryIndex = fileName.indexOf('?');
            if (queryIndex != -1) {
                fileName = fileName.substring(0, queryIndex);
            }
            
            int fragmentIndex = fileName.indexOf('#');
            if (fragmentIndex != -1) {
                fileName = fileName.substring(0, fragmentIndex);
            }
            
            // Sanitize filename for Windows
            fileName = sanitizeFileName(fileName);
            
            return fileName;
            
        } catch (MalformedURLException | URISyntaxException e) {
            return "download_" + System.currentTimeMillis();
        }
    }
    
    /**
     * Guess file extension from URL
     */
    private static String guessFileExtension(String urlString) {
        String url = urlString.toLowerCase();
        
        // Common file extensions
        if (url.contains(".zip")) return "zip";
        if (url.contains(".pdf")) return "pdf";
        if (url.contains(".mp4")) return "mp4";
        if (url.contains(".mp3")) return "mp3";
        if (url.contains(".jpg") || url.contains(".jpeg")) return "jpg";
        if (url.contains(".png")) return "png";
        if (url.contains(".gif")) return "gif";
        if (url.contains(".txt")) return "txt";
        if (url.contains(".doc")) return "doc";
        if (url.contains(".exe")) return "exe";
        if (url.contains(".msi")) return "msi";
        if (url.contains(".dmg")) return "dmg";
        if (url.contains(".iso")) return "iso";
        
        return "";
    }
    
    /**
     * Sanitize filename for Windows file system
     */
    private static String sanitizeFileName(String fileName) {
        // Remove or replace invalid characters for Windows
        String sanitized = fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
        
        // Remove control characters
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        // Trim spaces and dots from the end
        sanitized = sanitized.replaceAll("[ .]+$", "");
        
        // If empty after sanitization, provide default
        if (sanitized.isEmpty()) {
            sanitized = "download";
        }
        
        // Limit length to avoid path length issues
        if (sanitized.length() > 100) {
            String name = sanitized.substring(0, 85);
            String extension = "";
            int lastDot = sanitized.lastIndexOf('.');
            if (lastDot > 85) {
                extension = sanitized.substring(lastDot);
            }
            sanitized = name + extension;
        }
        
        return sanitized;
    }
    
    /**
     * Format bytes to human readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 0) return "Unknown";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        if (unitIndex == 0) {
            return String.format("%.0f %s", size, units[unitIndex]);
        } else {
            return String.format("%.1f %s", size, units[unitIndex]);
        }
    }
    
    /**
     * Format time in seconds to human readable format
     */
    public static String formatTime(long seconds) {
        if (seconds < 0) return "Unknown";
        
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
    
    /**
     * Validate URL format
     */
    public static boolean isValidUrl(String urlString) {
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            String protocol = url.getProtocol().toLowerCase();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * Get file size in human readable format
     */
    public static String getFileSizeString(File file) {
        if (!file.exists()) {
            return "File not found";
        }
        return formatBytes(file.length());
    }
    
    /**
     * Check if file exists and get info
     */
    public static void printFileInfo(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("File exists: " + filePath);
            System.out.println("Size: " + formatBytes(file.length()));
            System.out.println("Last modified: " + new Date(file.lastModified()));
        } else {
            System.out.println("File does not exist: " + filePath);
        }
    }
    
    /**
     * Create directory if it doesn't exist
     */
    public static boolean ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }
    
    /**
     * Log error to file
     */
    public static void logError(String message, Exception e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("[" + DATE_FORMAT.format(new Date()) + "] ERROR: " + message);
            if (e != null) {
                writer.println("Exception: " + e.getMessage());
                e.printStackTrace(writer);
            }
            writer.println("----------------------------------------");
        } catch (IOException ioException) {
            System.err.println("Failed to write to log file: " + ioException.getMessage());
        }
    }
    
    /**
     * Log info to file
     */
    public static void logInfo(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("[" + DATE_FORMAT.format(new Date()) + "] INFO: " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Calculate download speed
     */
    public static long calculateSpeed(long bytesDownloaded, long timeElapsedMs) {
        if (timeElapsedMs <= 0) return 0;
        return (bytesDownloaded * 1000) / timeElapsedMs; // bytes per second
    }
    
    /**
     * Calculate ETA (Estimated Time of Arrival)
     */
    public static long calculateETA(long totalSize, long downloadedSize, long currentSpeed) {
        if (currentSpeed <= 0 || downloadedSize >= totalSize) return -1;
        return (totalSize - downloadedSize) / currentSpeed; // seconds
    }
    
    /**
     * Check available disk space
     */
    public static boolean hasEnoughDiskSpace(String path, long requiredBytes) {
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (parent == null) {
                parent = new File(".");
            }
            
            long freeSpace = parent.getFreeSpace();
            return freeSpace >= requiredBytes;
        } catch (Exception e) {
            return true; // Assume we have space if we can't check
        }
    }
    
    /**
     * Clean up temporary files
     */
    public static void cleanupTempFiles(String basePath) {
        try {
            File baseFile = new File(basePath);
            File parentDir = baseFile.getParentFile();
            String baseName = baseFile.getName();
            
            if (parentDir != null && parentDir.exists()) {
                File[] files = parentDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName();
                        if (fileName.startsWith(baseName + ".part") || 
                            fileName.equals(baseName + ".partial")) {
                            if (file.delete()) {
                                System.out.println("Cleaned up: " + fileName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up temp files: " + e.getMessage());
        }
    }
    
    /**
     * Get current timestamp
     */
    public static String getCurrentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }
}
