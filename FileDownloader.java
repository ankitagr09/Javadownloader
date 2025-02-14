import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileDownloader {

    private JFrame frame;
    private JTextField urlTextField;
    private JTextField saveLocationTextField;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton downloadButton, pauseButton, fetchUrlButton;
    private boolean isPaused = false;
    private long downloadedBytes = 0;
    private long fileSize = 0;
    private List<String> downloadQueue = new ArrayList<>();

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
        frame.setSize(1000, 600);
        frame.setTitle("Modern File Downloader");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center the window
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background
    }

    private void addUIComponents() {
        // Load Poppins Font from Google Fonts
        Font poppinsFont;
        try {
            poppinsFont = Font.createFont(Font.TRUETYPE_FONT, new URL("https://fonts.googleapis.com/css2?family=Poppins").openStream())
                    .deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(poppinsFont);
        } catch (Exception e) {
            poppinsFont = new Font("poppins", Font.PLAIN, 16); // Fallback font
        }

        // Title Label
        JLabel titleLabel = new JLabel("Modern File Downloader");
        titleLabel.setFont(poppinsFont.deriveFont(Font.BOLD, 28));
        titleLabel.setBounds(350, 20, 400, 40);
        titleLabel.setForeground(new Color(33, 150, 243)); // Material Blue
        frame.add(titleLabel);

        // URL Label and TextField
        JLabel urlLabel = new JLabel("Enter URL to Download File:");
        urlLabel.setFont(poppinsFont.deriveFont(Font.PLAIN, 16));
        urlLabel.setBounds(50, 80, 300, 25);
        frame.add(urlLabel);

        urlTextField = new JTextField("https://example.com/file.zip");
        urlTextField.setFont(poppinsFont.deriveFont(Font.PLAIN, 14));
        urlTextField.setBounds(50, 110, 700, 40);
        urlTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        frame.add(urlTextField);

        // Fetch URL Button
        fetchUrlButton = createModernButton("Fetch URL", new Color(255, 193, 7), new Color(255, 152, 0));
        fetchUrlButton.setBounds(760, 110, 120, 40);
        frame.add(fetchUrlButton);

        fetchUrlButton.addActionListener(e -> fetchUrlFromClipboard());

        // Save Location Label and TextField
        JLabel saveLocationLabel = new JLabel("Save File To:");
        saveLocationLabel.setFont(poppinsFont.deriveFont(Font.PLAIN, 16));
        saveLocationLabel.setBounds(50, 170, 300, 25);
        frame.add(saveLocationLabel);

        saveLocationTextField = new JTextField(System.getProperty("user.home") + "/Downloads");
        saveLocationTextField.setFont(poppinsFont.deriveFont(Font.PLAIN, 14));
        saveLocationTextField.setBounds(50, 200, 600, 40);
        saveLocationTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        frame.add(saveLocationTextField);

        // Browse Button
        JButton browseButton = createModernButton("Browse", new Color(33, 150, 243), new Color(25, 118, 210));
        browseButton.setBounds(660, 200, 90, 40);
        frame.add(browseButton);

        browseButton.addActionListener(e -> browseForSaveLocation());

        // Download Button
        downloadButton = createModernButton("Start Download", new Color(76, 175, 80), new Color(56, 142, 60));
        downloadButton.setBounds(50, 270, 150, 45);
        frame.add(downloadButton);

        downloadButton.addActionListener(e -> startDownload());

        // Pause Button
        pauseButton = createModernButton("Pause", new Color(244, 67, 54), new Color(211, 47, 47));
        pauseButton.setBounds(220, 270, 100, 45);
        pauseButton.setEnabled(false); // Disabled by default
        frame.add(pauseButton);

        pauseButton.addActionListener(e -> togglePause());

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(50, 340, 800, 30);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(33, 150, 243)); // Material Blue
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        frame.add(progressBar);

        // Status Label
        statusLabel = new JLabel("Ready to download");
        statusLabel.setFont(poppinsFont.deriveFont(Font.PLAIN, 14));
        statusLabel.setBounds(50, 390, 800, 25);
        frame.add(statusLabel);
    }

    private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void fetchUrlFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
            if (clipboardText != null && clipboardText.startsWith("http")) {
                urlTextField.setText(clipboardText);
                statusLabel.setText("URL fetched from clipboard.");
            } else {
                statusLabel.setText("No valid URL found in clipboard.");
            }
        } catch (UnsupportedFlavorException | IOException e) {
            statusLabel.setText("Failed to fetch URL from clipboard.");
        }
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