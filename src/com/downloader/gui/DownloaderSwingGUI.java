package com.downloader.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import com.downloader.Downloader;
import com.downloader.Utils;

/**
 * Swing GUI for Java Smart Downloader
 */
public class DownloaderSwingGUI extends JFrame {
    
    // UI Components
    private JTextField urlField;
    private JTextField destinationField;
    private JSpinner threadSpinner;
    private JButton browseButton;
    private JButton downloadButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton clearButton;
    
    // Progress components
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel speedLabel;
    private JLabel etaLabel;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    // Download management
    private GUIDownloader currentDownloader;
    private Thread downloadThread;
    private AtomicBoolean isDownloading = new AtomicBoolean(false);
    
    public DownloaderSwingGUI() {
        setTitle("Java Smart Downloader - GUI Version");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(750, 650);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set system look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        updateUIState();
        
        logMessage("Java Smart Downloader GUI Started");
        logMessage("Ready to download files!");
    }
    
    private void initializeComponents() {
        // Input components
        urlField = new JTextField();
        urlField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        destinationField = new JTextField(System.getProperty("user.dir"));
        destinationField.setEditable(false);
        destinationField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        threadSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        threadSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        // Buttons
        browseButton = new JButton("Browse...");
        browseButton.setBackground(new Color(76, 175, 80));
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        
        downloadButton = new JButton("▶ Start Download");
        downloadButton.setBackground(new Color(33, 150, 243));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        downloadButton.setFocusPainted(false);
        
        pauseButton = new JButton("⏸ Pause");
        pauseButton.setBackground(new Color(255, 152, 0));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        
        resumeButton = new JButton("▶ Resume");
        resumeButton.setBackground(new Color(76, 175, 80));
        resumeButton.setForeground(Color.WHITE);
        resumeButton.setFocusPainted(false);
        
        clearButton = new JButton("🗑 Clear");
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        
        // Progress components
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready to download");
        progressBar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        progressLabel = new JLabel("Ready to download");
        speedLabel = new JLabel("Speed: --");
        etaLabel = new JLabel("ETA: --");
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setForeground(new Color(33, 150, 243));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // Log area
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setBackground(new Color(248, 248, 248));
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Input section
        JPanel inputPanel = createInputPanel();
        
        // Progress section
        JPanel progressPanel = createProgressPanel();
        
        // Control buttons
        JPanel buttonPanel = createButtonPanel();
        
        // Log section
        JPanel logPanel = createLogPanel();
        
        // Combine sections
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(15, 10, 15, 10));
        
        JLabel titleLabel = new JLabel("🚀 Java Smart Downloader");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 150, 243));
        
        JLabel subtitleLabel = new JLabel("Multi-threaded file downloader with pause/resume support");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitleLabel);
        
        panel.add(titleBox);
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "📥 Download Settings", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.BOLD, 12)));
        panel.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // URL row
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("URL:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(urlField, gbc);
        
        // Destination row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(destinationField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(browseButton, gbc);
        
        // Threads row
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Threads:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        JPanel threadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        threadPanel.setOpaque(false);
        threadPanel.add(threadSpinner);
        threadPanel.add(Box.createHorizontalStrut(10));
        threadPanel.add(new JLabel("(1-8 threads)"));
        panel.add(threadPanel, gbc);
        
        return panel;
    }
    
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "📊 Download Progress", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.BOLD, 12)));
        panel.setBackground(new Color(249, 249, 249));
        
        JPanel progressBarPanel = new JPanel(new BorderLayout());
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        statsPanel.add(speedLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(etaLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(statusLabel);
        
        panel.add(progressBarPanel, BorderLayout.NORTH);
        panel.add(progressLabel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        
        downloadButton.setPreferredSize(new Dimension(150, 35));
        pauseButton.setPreferredSize(new Dimension(100, 35));
        resumeButton.setPreferredSize(new Dimension(100, 35));
        clearButton.setPreferredSize(new Dimension(100, 35));
        
        panel.add(downloadButton);
        panel.add(pauseButton);
        panel.add(resumeButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "📝 Activity Log", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.BOLD, 12)));
        
        panel.add(logScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Window close handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isDownloading.get()) {
                    int choice = JOptionPane.showConfirmDialog(
                        DownloaderSwingGUI.this,
                        "Download is in progress. Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        if (currentDownloader != null) {
                            currentDownloader.cancelDownload();
                        }
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        
        // Browse button
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Select Download Destination");
            
            String currentPath = destinationField.getText();
            if (!currentPath.isEmpty()) {
                File currentDir = new File(currentPath);
                if (currentDir.exists()) {
                    chooser.setCurrentDirectory(currentDir);
                }
            }
            
            if (chooser.showOpenDialog(DownloaderSwingGUI.this) == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                destinationField.setText(selectedDir.getAbsolutePath());
                logMessage("Destination set to: " + selectedDir.getAbsolutePath());
            }
            }
        });
        
        // Control buttons
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDownload();
            }
        });
        
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseDownload();
            }
        });
        
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeDownload();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }
    
    private void startDownload() {
        String url = urlField.getText().trim();
        String destination = destinationField.getText().trim();
        int threads = (Integer) threadSpinner.getValue();
        
        // Validation
        if (url.isEmpty()) {
            showError("Please enter a valid URL");
            return;
        }
        
        if (!Utils.isValidUrl(url)) {
            showError("Invalid URL format. Please use HTTP or HTTPS URLs.");
            return;
        }
        
        if (destination.isEmpty()) {
            showError("Please select a destination folder");
            return;
        }
        
        File destDir = new File(destination);
        if (!destDir.exists() && !destDir.mkdirs()) {
            showError("Could not create destination directory");
            return;
        }
        
        // Extract filename and create full path
        String fileName = Utils.extractFileName(url);
        String fullPath = destination + File.separator + fileName;
        
        logMessage("Starting download...");
        logMessage("URL: " + url);
        logMessage("Destination: " + fullPath);
        logMessage("Threads: " + threads);
        
        // Create downloader
        currentDownloader = new GUIDownloader(url, fullPath, threads);
        
        // Start download in background thread
        isDownloading.set(true);
        updateUIState();
        
        downloadThread = new Thread(() -> {
            try {
                currentDownloader.startDownload();
                SwingUtilities.invokeLater(() -> {
                    isDownloading.set(false);
                    updateUIState();
                    logMessage("✅ Download completed successfully!");
                    showInfo("Download completed successfully!\nFile saved to: " + fullPath);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    isDownloading.set(false);
                    updateUIState();
                    logMessage("❌ Download failed: " + e.getMessage());
                    showError("Download failed: " + e.getMessage());
                });
            }
        });
        
        downloadThread.setDaemon(true);
        downloadThread.start();
    }
    
    private void pauseDownload() {
        if (currentDownloader != null && isDownloading.get()) {
            currentDownloader.pauseDownload();
            isDownloading.set(false);
            updateUIState();
            logMessage("⏸ Download paused");
        }
    }
    
    private void resumeDownload() {
        if (currentDownloader != null && !isDownloading.get()) {
            isDownloading.set(true);
            updateUIState();
            
            downloadThread = new Thread(() -> {
                try {
                    currentDownloader.resumeDownload();
                    SwingUtilities.invokeLater(() -> {
                        isDownloading.set(false);
                        updateUIState();
                        logMessage("✅ Download completed successfully!");
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        isDownloading.set(false);
                        updateUIState();
                        logMessage("❌ Download failed: " + e.getMessage());
                    });
                }
            });
            
            downloadThread.setDaemon(true);
            downloadThread.start();
            
            logMessage("▶ Download resumed");
        }
    }
    
    private void clearAll() {
        if (isDownloading.get()) {
            showError("Cannot clear while download is in progress. Please pause first.");
            return;
        }
        
        urlField.setText("");
        destinationField.setText(System.getProperty("user.dir"));
        threadSpinner.setValue(4);
        progressBar.setValue(0);
        progressBar.setString("Ready to download");
        progressLabel.setText("Ready to download");
        speedLabel.setText("Speed: --");
        etaLabel.setText("ETA: --");
        statusLabel.setText("Status: Ready");
        logArea.setText("");
        
        currentDownloader = null;
        
        logMessage("All fields cleared. Ready for new download.");
    }
    
    private void updateUIState() {
        boolean downloading = isDownloading.get();
        boolean hasDownloader = currentDownloader != null;
        
        downloadButton.setEnabled(!downloading);
        pauseButton.setEnabled(downloading);
        resumeButton.setEnabled(!downloading && hasDownloader && !currentDownloader.isCompleted());
        clearButton.setEnabled(!downloading);
        
        urlField.setEnabled(!downloading);
        browseButton.setEnabled(!downloading);
        threadSpinner.setEnabled(!downloading);
        
        if (downloading) {
            statusLabel.setText("Status: Downloading...");
            statusLabel.setForeground(new Color(76, 175, 80));
        } else if (hasDownloader && currentDownloader.isCompleted()) {
            statusLabel.setText("Status: Completed");
            statusLabel.setForeground(new Color(76, 175, 80));
        } else if (hasDownloader) {
            statusLabel.setText("Status: Paused");
            statusLabel.setForeground(new Color(255, 152, 0));
        } else {
            statusLabel.setText("Status: Ready");
            statusLabel.setForeground(new Color(33, 150, 243));
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = Utils.getCurrentTimestamp();
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    // Update progress from downloader
    public void updateProgress(double progress, String progressText, String speed, String eta) {
        SwingUtilities.invokeLater(() -> {
            int progressPercent = (int) (progress * 100);
            progressBar.setValue(progressPercent);
            progressBar.setString(progressPercent + "%");
            progressLabel.setText(progressText);
            speedLabel.setText("Speed: " + speed);
            etaLabel.setText("ETA: " + eta);
        });
    }
    
    // Inner class for GUI-aware downloader
    private class GUIDownloader extends Downloader {
        
        public GUIDownloader(String url, String destinationPath, int numThreads) {
            super(url, destinationPath, numThreads);
        }
        
        // Override to provide GUI updates
        // We'll simulate progress updates since the base class doesn't expose callbacks
        @Override
        public void startDownload() {
            // Start a progress updater thread
            Thread progressUpdater = new Thread(() -> {
                while (isDownloading.get() && !isCompleted()) {
                    try {
                        Thread.sleep(1000); // Update every second
                        
                        // This is a simplified progress update
                        // In a real implementation, you'd modify Downloader to expose progress callbacks
                        if (isDownloading.get()) {
                            updateProgress(0.5, "Downloading...", "-- KB/s", "-- sec");
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            progressUpdater.setDaemon(true);
            progressUpdater.start();
            
            super.startDownload();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DownloaderSwingGUI().setVisible(true);
        });
    }
}
