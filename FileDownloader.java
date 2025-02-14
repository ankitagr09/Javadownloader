import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class FileDownloader {

    private JFrame frame;
    private JTextField urlTextField;
    private JTextField saveLocationTextField;
    private JLabel statusLabel;

    public static void main(String[] args) {
        // Create an instance of FileDownloader and call the method to show the GUI
        new FileDownloader().createAndShowGUI();
    }

    public void createAndShowGUI() {
        initializeFrame();
        addUIComponents();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame();
        frame.setSize(750, 310);
        frame.setTitle("Fastest File Downloader");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.getContentPane().setBackground(Color.WHITE);
    }

    private void addUIComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("<html><u style='color:navy;'>Faster File Downloader</u><html>");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
        titleLabel.setBounds(250, 10, 350, 30);
        titleLabel.setForeground(Color.RED);
        frame.add(titleLabel);

        // URL Label and TextField
        JLabel urlLabel = new JLabel("<html><p style='color:navy; font-style:italic;'>Enter URL to Download file</p></html>");
        urlLabel.setBounds(35, 60, 500, 20);
        urlLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        frame.add(urlLabel);

        urlTextField = new JTextField("Paste your URL here");
        urlTextField.setFont(new Font("Times New Roman", Font.BOLD, 15));
        urlTextField.setForeground(Color.RED);
        urlTextField.setBounds(35, 85, 550, 30);
        frame.add(urlTextField);

        // Save Location Label and TextField
        JLabel saveLocationLabel = new JLabel("<html><p style='color:navy; font-style:italic;'>Enter Location to save file</p></html>");
        saveLocationLabel.setBounds(35, 125, 500, 20);
        saveLocationLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        frame.add(saveLocationLabel);

        saveLocationTextField = new JTextField("Preferred location to download file");
        saveLocationTextField.setFont(new Font("Times New Roman", Font.BOLD, 15));
        saveLocationTextField.setForeground(Color.RED);
        saveLocationTextField.setBounds(35, 150, 450, 30);
        frame.add(saveLocationTextField);

        // Browse Button
        JButton browseButton = new JButton("<html><p style='color:navy; font-style:italic;'>Browse</p></html>");
        browseButton.setBackground(Color.WHITE);
        browseButton.setBounds(490, 150, 90, 30);
        frame.add(browseButton);

        browseButton.addActionListener(e -> browseForSaveLocation());

        // Download Button
        JButton downloadButton = new JButton("<html><p style='color:navy; font-style:italic;'>Start Download</p></html>");
        downloadButton.setBackground(Color.WHITE);
        downloadButton.setBounds(260, 200, 150, 30);
        frame.add(downloadButton);

        downloadButton.addActionListener(e -> startDownload());

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setBounds(220, 240, 400, 30);
        statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        frame.add(statusLabel);
    }

    private void browseForSaveLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Select Directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveLocationTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            saveLocationTextField.setText("No Selection");
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

        new Thread(() -> downloadFile(url, outputFile)).start();
    }

    private void downloadFile(String url, File outputFile) {
        try {
            URL downloadUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            long fileSize = connection.getContentLengthLong();

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fos = new FileOutputStream(outputFile);
                 BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)) {

                byte[] buffer = new byte[1024];
                long downloaded = 0;
                int read;
                while ((read = in.read(buffer, 0, 1024)) >= 0) {
                    bout.write(buffer, 0, read);
                    downloaded += read;
                    double percentDownloaded = (downloaded * 100.0) / fileSize;
                    statusLabel.setText(String.format("Downloaded %.2f%% of file", percentDownloaded));
                }
                statusLabel.setText("Download Completed");
            }
        } catch (IOException e) {
            statusLabel.setText("Error during download: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}