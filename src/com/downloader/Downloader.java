package com.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded downloader with pause/resume support
 */
public class Downloader {
    private final String url;
    private final String destinationPath;
    private final int numThreads;
    private final AtomicLong totalBytesDownloaded;
    private final AtomicBoolean isPaused;
    private final AtomicBoolean isCompleted;
    private final AtomicBoolean isCancelled;
    
    private long fileSize;
    private ExecutorService executor;
    private List<Future<?>> downloadTasks;
    private List<DownloadWorker> workers;
    private long startTime;
    
    // Progress tracking
    private Thread progressThread;
    private final AtomicBoolean showProgress;
    
    public Downloader(String url, String destinationPath, int numThreads) {
        this.url = url;
        this.destinationPath = destinationPath;
        this.numThreads = numThreads;
        this.totalBytesDownloaded = new AtomicLong(0);
        this.isPaused = new AtomicBoolean(false);
        this.isCompleted = new AtomicBoolean(false);
        this.isCancelled = new AtomicBoolean(false);
        this.showProgress = new AtomicBoolean(false);
        this.downloadTasks = new ArrayList<>();
        this.workers = new ArrayList<>();
    }
    
    public void startDownload() {
        try {
            // Get file size and check if server supports partial downloads
            if (!initializeDownload()) {
                return;
            }
            
            startTime = System.currentTimeMillis();
            
            // Check for existing partial file
            File partialFile = new File(destinationPath + ".partial");
            if (partialFile.exists()) {
                System.out.println("Found partial download. Resuming from: " + 
                    Utils.formatBytes(partialFile.length()));
                totalBytesDownloaded.set(partialFile.length());
            }
            
            // Start download workers
            startDownloadWorkers();
            
            // Start progress tracking
            startProgressTracking();
            
        } catch (Exception e) {
            System.err.println("Error starting download: " + e.getMessage());
            Utils.logError("Download start failed", e);
        }
    }
    
    private boolean initializeDownload() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK && 
                responseCode != HttpURLConnection.HTTP_PARTIAL) {
                System.err.println("Server returned error code: " + responseCode);
                return false;
            }
            
            fileSize = connection.getContentLengthLong();
            if (fileSize <= 0) {
                System.out.println("Warning: Unable to determine file size. Using single-threaded download.");
                fileSize = -1;
            } else {
                System.out.println("File size: " + Utils.formatBytes(fileSize));
            }
            
            // Check if server supports partial downloads
            String acceptRanges = connection.getHeaderField("Accept-Ranges");
            if (!"bytes".equals(acceptRanges) && fileSize > 0) {
                System.out.println("Server doesn't support partial downloads. Using single thread.");
            }
            
            connection.disconnect();
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to initialize download: " + e.getMessage());
            Utils.logError("Download initialization failed", e);
            return false;
        }
    }
    
    private void startDownloadWorkers() {
        executor = Executors.newFixedThreadPool(numThreads);
        
        if (fileSize <= 0) {
            // Single-threaded download for unknown file size
            DownloadWorker worker = new DownloadWorker(0, 0, -1);
            workers.add(worker);
            Future<?> task = executor.submit(worker);
            downloadTasks.add(task);
        } else {
            // Multi-threaded download
            long chunkSize = fileSize / numThreads;
            
            for (int i = 0; i < numThreads; i++) {
                long startByte = i * chunkSize;
                long endByte = (i == numThreads - 1) ? fileSize - 1 : (startByte + chunkSize - 1);
                
                DownloadWorker worker = new DownloadWorker(i, startByte, endByte);
                workers.add(worker);
                Future<?> task = executor.submit(worker);
                downloadTasks.add(task);
            }
        }
    }
    
    private void startProgressTracking() {
        showProgress.set(true);
        progressThread = new Thread(() -> {
            while (showProgress.get() && !isCompleted.get() && !isCancelled.get()) {
                try {
                    Thread.sleep(1000); // Update every second
                    if (!isPaused.get()) {
                        displayProgress();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();
    }
    
    private void displayProgress() {
        long downloaded = totalBytesDownloaded.get();
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (fileSize > 0) {
            double percentage = (double) downloaded / fileSize * 100;
            long speed = elapsed > 0 ? (downloaded * 1000) / elapsed : 0;
            long eta = speed > 0 ? (fileSize - downloaded) / speed : -1;
            
            String progressBar = createProgressBar(percentage);
            
            System.out.printf("\r[%s] %.1f%% | %s/%s | Speed: %s/s | ETA: %s",
                progressBar, percentage,
                Utils.formatBytes(downloaded), Utils.formatBytes(fileSize),
                Utils.formatBytes(speed),
                eta >= 0 ? Utils.formatTime(eta) : "Unknown");
        } else {
            long speed = elapsed > 0 ? (downloaded * 1000) / elapsed : 0;
            System.out.printf("\rDownloaded: %s | Speed: %s/s | Time: %s",
                Utils.formatBytes(downloaded),
                Utils.formatBytes(speed),
                Utils.formatTime(elapsed / 1000));
        }
    }
    
    private String createProgressBar(double percentage) {
        int barLength = 30;
        int filled = (int) (percentage / 100 * barLength);
        StringBuilder bar = new StringBuilder();
        
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        return bar.toString();
    }
    
    public void pauseDownload() {
        isPaused.set(true);
        System.out.println("\nPausing download...");
        
        // Cancel all tasks
        for (Future<?> task : downloadTasks) {
            task.cancel(true);
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
        
        showProgress.set(false);
        System.out.println("Download paused. Progress saved to partial file.");
    }
    
    public void resumeDownload() {
        if (isCompleted.get()) {
            System.out.println("Download is already completed.");
            return;
        }
        
        isPaused.set(false);
        downloadTasks.clear();
        workers.clear();
        
        startDownloadWorkers();
        startProgressTracking();
        
        System.out.println("Download resumed.");
    }
    
    public void cancelDownload() {
        isCancelled.set(true);
        isPaused.set(true);
        showProgress.set(false);
        
        for (Future<?> task : downloadTasks) {
            task.cancel(true);
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
    }
    
    public boolean isDownloading() {
        return !isPaused.get() && !isCompleted.get() && !isCancelled.get();
    }
    
    public boolean isCompleted() {
        return isCompleted.get();
    }
    
    public void printStatus() {
        long downloaded = totalBytesDownloaded.get();
        System.out.println("Download Status:");
        System.out.println("URL: " + url);
        System.out.println("Destination: " + destinationPath);
        System.out.println("Downloaded: " + Utils.formatBytes(downloaded));
        
        if (fileSize > 0) {
            double percentage = (double) downloaded / fileSize * 100;
            System.out.println("Progress: " + String.format("%.1f%%", percentage));
            System.out.println("File size: " + Utils.formatBytes(fileSize));
        }
        
        System.out.println("Status: " + 
            (isCompleted.get() ? "Completed" :
             isPaused.get() ? "Paused" :
             isCancelled.get() ? "Cancelled" : "Downloading"));
    }
    
    private class DownloadWorker implements Runnable {
        private final int workerId;
        private final long startByte;
        private final long endByte;
        
        public DownloadWorker(int workerId, long startByte, long endByte) {
            this.workerId = workerId;
            this.startByte = startByte;
            this.endByte = endByte;
        }
        
        @Override
        public void run() {
            String partFileName = destinationPath + ".part" + workerId;
            
            try {
                // Check for existing partial download
                File partFile = new File(partFileName);
                long currentPos = startByte;
                
                if (partFile.exists()) {
                    currentPos += partFile.length();
                }
                
                if (endByte > 0 && currentPos > endByte) {
                    // This part is already complete
                    return;
                }
                
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                
                if (fileSize > 0 && endByte > 0) {
                    // Set range for partial download
                    String range = "bytes=" + currentPos + "-" + endByte;
                    connection.setRequestProperty("Range", range);
                }
                
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK && 
                    responseCode != HttpURLConnection.HTTP_PARTIAL) {
                    throw new IOException("Server returned error code: " + responseCode);
                }
                
                try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream out = new FileOutputStream(partFileName, partFile.exists())) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    
                    while ((bytesRead = in.read(buffer)) != -1 && !isPaused.get()) {
                        out.write(buffer, 0, bytesRead);
                        totalBytesDownloaded.addAndGet(bytesRead);
                    }
                }
                
                connection.disconnect();
                
                // Check if all parts are complete
                checkAndMergeFiles();
                
            } catch (Exception e) {
                if (!isPaused.get()) {
                    System.err.println("\nError in worker " + workerId + ": " + e.getMessage());
                    Utils.logError("Worker " + workerId + " failed", e);
                }
            }
        }
    }
    
    private synchronized void checkAndMergeFiles() {
        // Check if all workers are done
        boolean allComplete = true;
        for (int i = 0; i < workers.size(); i++) {
            String partFileName = destinationPath + ".part" + i;
            File partFile = new File(partFileName);
            if (!partFile.exists()) {
                allComplete = false;
                break;
            }
        }
        
        if (allComplete && !isCompleted.get()) {
            mergeFiles();
        }
    }
    
    private void mergeFiles() {
        try {
            showProgress.set(false);
            System.out.println("\nMerging files...");
            
            try (FileOutputStream finalFile = new FileOutputStream(destinationPath)) {
                for (int i = 0; i < workers.size(); i++) {
                    String partFileName = destinationPath + ".part" + i;
                    File partFile = new File(partFileName);
                    
                    if (partFile.exists()) {
                        try (FileInputStream partStream = new FileInputStream(partFile)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = partStream.read(buffer)) != -1) {
                                finalFile.write(buffer, 0, bytesRead);
                            }
                        }
                        partFile.delete(); // Clean up part file
                    }
                }
            }
            
            // Clean up partial file if it exists
            File partialFile = new File(destinationPath + ".partial");
            if (partialFile.exists()) {
                partialFile.delete();
            }
            
            isCompleted.set(true);
            long totalTime = (System.currentTimeMillis() - startTime) / 1000;
            
            System.out.println("\n✅ Download completed successfully!");
            System.out.println("File saved to: " + destinationPath);
            System.out.println("Total time: " + Utils.formatTime(totalTime));
            System.out.println("Average speed: " + 
                Utils.formatBytes(totalBytesDownloaded.get() / Math.max(totalTime, 1)) + "/s");
            
        } catch (Exception e) {
            System.err.println("Error merging files: " + e.getMessage());
            Utils.logError("File merge failed", e);
        }
    }
}
