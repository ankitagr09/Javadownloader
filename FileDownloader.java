import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    private JFrame frame;
    private JTextField urlTextField;
    private JTextField saveLocationTextField;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton downloadButton, pauseButton;
    private boolean isPaused = false;
    private long downloadedBytes = 0;
    private long fileSize = 0;

    public static void main(String[] args) {
        new FileDownloader().createAndShowGUI();
    }

    public void createAndShowGUI() {
        initializeFrame();
        addUIComponents();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame();
        frame.setSize(800, 500);
        frame.setTitle("Modern File Downloader");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center the window
        frame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background
    }

    private void addUIComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Modern File Downloader");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setBounds(250, 20, 400, 40);
        titleLabel.setForeground(new Color(33, 150, 243)); // Material Blue
        frame.add(titleLabel);

        // URL Label and TextField
        JLabel urlLabel = new JLabel("Enter URL to Download File:");
        urlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        urlLabel.setBounds(50, 80, 300, 25);
        frame.add(urlLabel);

        urlTextField = new JTextField("https://example.com/file.zip");
        urlTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        urlTextField.setBounds(50, 110, 600, 35);
        frame.add(urlTextField);

        // Save Location Label and TextField
        JLabel saveLocationLabel = new JLabel("Save File To:");
        saveLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        saveLocationLabel.setBounds(50, 160, 300, 25);
        frame.add(saveLocationLabel);

        saveLocationTextField = new JTextField(System.getProperty("user.home") + "/Downloads");
        saveLocationTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveLocationTextField.setBounds(50, 190, 500, 35);
        frame.add(saveLocationTextField);

        // Browse Button
        JButton browseButton = new JButton("Browse");
        browseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        browseButton.setBounds(560, 190, 90, 35);
        browseButton.setBackground(new Color(33, 150, 243)); // Material Blue
        browseButton.setForeground(Color.WHITE);
        frame.add(browseButton);

        browseButton.addActionListener(e -> browseForSaveLocation());

        // Download Button
        downloadButton = new JButton("Start Download");
        downloadButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        downloadButton.setBounds(50, 250, 150, 40);
        downloadButton.setBackground(new Color(76, 175, 80)); // Material Green
        downloadButton.setForeground(Color.WHITE);
        frame.add(downloadButton);

        downloadButton.addActionListener(e -> startDownload());

        // Pause Button
        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pauseButton.setBounds(220, 250, 100, 40);
        pauseButton.setBackground(new Color(244, 67, 54)); // Material Red
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setEnabled(false); // Disabled by default
        frame.add(pauseButton);

        pauseButton.addActionListener(e -> togglePause());

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(50, 310, 600, 25);
        progressBar.setStringPainted(true);
        frame.add(progressBar);

        // Status Label
        statusLabel = new JLabel("Ready to download");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBounds(50, 350, 600, 25);
        frame.add(statusLabel);
    }

    private void browseForSaveLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Select Directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveLocationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startDownload() {
        String url = urlTextField.getText();
        String saveLocation = saveLocationTextField.getText();

        if (url.isEmpty() || saveLocation.isEmpty()) {
            statusLabel.setText("Please provide both URL and save location.");
            return;
        }

        File outputFile = new File(saveLocation, getFileNameFromUrl(url));

        // Check if file already exists
        if (outputFile.exists()) {
            int response = JOptionPane.showConfirmDialog(frame, "File already exists. Overwrite?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        downloadButton.setEnabled(false);
        pauseButton.setEnabled(true);
        new Thread(() -> downloadFile(url, outputFile)).start();
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }

    private void downloadFile(String url, File outputFile) {
        try {
            URL downloadUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            fileSize = connection.getContentLengthLong();
            downloadedBytes = 0;

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fos = new FileOutputStream(outputFile);
                 BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)) {

                byte[] buffer = new byte[1024];
                int read;
                long startTime = System.currentTimeMillis();

                while ((read = in.read(buffer, 0, 1024)) >= 0) {
                    if (isPaused) {
                        Thread.sleep(100); // Pause download
                        continue;
                    }

                    bout.write(buffer, 0, read);
                    downloadedBytes += read;

                    // Update progress
                    double percentDownloaded = (downloadedBytes * 100.0) / fileSize;
                    progressBar.setValue((int) percentDownloaded);

                    // Calculate download speed
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    double speed = (downloadedBytes / 1024.0) / (elapsedTime / 1000.0); // KB/s

                    statusLabel.setText(String.format("Downloaded %.2f%% (%.2f KB/s)", percentDownloaded, speed));
                }

                statusLabel.setText("Download Completed");
            }
        } catch (IOException | InterruptedException e) {
            statusLabel.setText("Error: " + e.getMessage());
        } finally {
            downloadButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}