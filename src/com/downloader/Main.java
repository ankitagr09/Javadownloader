package com.downloader;

import java.io.File;
import java.util.Scanner;

/**
 * Main class for Java Smart Downloader (MVP)
 * Handles user input and coordinates the download process
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Downloader currentDownloader;
    
    public static void main(String[] args) {
        System.out.println("=== Java Smart Downloader (MVP) ===");
        System.out.println("Multi-threaded file downloader with pause/resume support\n");
        
        while (true) {
            displayMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1:
                    startNewDownload();
                    break;
                case 2:
                    pauseCurrentDownload();
                    break;
                case 3:
                    resumeDownload();
                    break;
                case 4:
                    showDownloadStatus();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    if (currentDownloader != null) {
                        currentDownloader.cancelDownload();
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }
    
    private static void displayMenu() {
        System.out.println("Choose an option:");
        System.out.println("1. Start new download");
        System.out.println("2. Pause current download");
        System.out.println("3. Resume download");
        System.out.println("4. Show download status");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void startNewDownload() {
        if (currentDownloader != null && currentDownloader.isDownloading()) {
            System.out.println("A download is already in progress. Please pause it first.");
            return;
        }
        
        System.out.print("Enter the URL to download: ");
        String url = scanner.nextLine().trim();
        
        if (url.isEmpty()) {
            System.out.println("URL cannot be empty.");
            return;
        }
        
        System.out.print("Enter destination directory (or press Enter for current directory): ");
        String destDir = scanner.nextLine().trim();
        if (destDir.isEmpty()) {
            destDir = System.getProperty("user.dir");
        }
        
        // Create destination directory if it doesn't exist
        File destDirFile = new File(destDir);
        if (!destDirFile.exists()) {
            if (!destDirFile.mkdirs()) {
                System.out.println("Failed to create destination directory.");
                return;
            }
        }
        
        System.out.print("Enter number of threads (1-8, default 4): ");
        String threadsInput = scanner.nextLine().trim();
        int numThreads = 4;
        
        if (!threadsInput.isEmpty()) {
            try {
                numThreads = Integer.parseInt(threadsInput);
                if (numThreads < 1 || numThreads > 8) {
                    numThreads = 4;
                    System.out.println("Invalid thread count. Using default: 4");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid thread count. Using default: 4");
            }
        }
        
        // Extract filename from URL
        String fileName = Utils.extractFileName(url);
        String destinationPath = destDir + File.separator + fileName;
        
        System.out.println("\nStarting download:");
        System.out.println("URL: " + url);
        System.out.println("Destination: " + destinationPath);
        System.out.println("Threads: " + numThreads);
        System.out.println("----------------------------------------");
        
        currentDownloader = new Downloader(url, destinationPath, numThreads);
        currentDownloader.startDownload();
    }
    
    private static void pauseCurrentDownload() {
        if (currentDownloader == null) {
            System.out.println("No download in progress.");
            return;
        }
        
        if (!currentDownloader.isDownloading()) {
            System.out.println("No active download to pause.");
            return;
        }
        
        currentDownloader.pauseDownload();
        System.out.println("Download paused. You can resume it later.");
    }
    
    private static void resumeDownload() {
        if (currentDownloader == null) {
            System.out.println("No download to resume. Please start a new download.");
            return;
        }
        
        if (currentDownloader.isDownloading()) {
            System.out.println("Download is already in progress.");
            return;
        }
        
        if (currentDownloader.isCompleted()) {
            System.out.println("Download is already completed.");
            return;
        }
        
        System.out.println("Resuming download...");
        currentDownloader.resumeDownload();
    }
    
    private static void showDownloadStatus() {
        if (currentDownloader == null) {
            System.out.println("No download initiated.");
            return;
        }
        
        currentDownloader.printStatus();
    }
}
