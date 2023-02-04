package downloader;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

class Downloader extends Thread{
	

	static JFrame f1;
	static JFileChooser chooser;
	static File file1;
	static JButton b1, b2, b3;
	static JTextField t1, t2;
	static JLabel l1, l2, l3, l4;
	static JProgressBar pb1;
	static URL url;

	static String link; // download url
	static File out; // file

	public Downloader(String link, File out) {
		this.link = link;
		this.out = out;
	}

	static double percentDownloaded;

	public void run()
	{
		try
		{
			URL url=new URL(link);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			double filesize = (double)http.getContentLengthLong();
			
			BufferedInputStream in = new BufferedInputStream(http.getInputStream());
			@SuppressWarnings("static-access")
			FileOutputStream fos = new FileOutputStream(this.out);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte[] buffer = new byte[1024];
			double Downloaded = 0.00;
			int read=0;
			percentDownloaded = 0.00;
			while((read=in.read(buffer,0,1024))>=0) {
			bout.write(buffer,0,read);	
			Downloaded+=read;
			percentDownloaded = (Downloaded*100)/filesize;
		//	String percent = String.format("%4F", percentDownloaded);
			l4.setText("Downloaded " + percentDownloaded + " % of file");
			
		
			//l4.setText("Downloaded " + percent + " % of file");
			}
			bout.close();
			in.close();
			l4.setText("Download Completed");
		//	System.out.println("Download Completed");
			
			
		
	}
		catch(Exception r) {
			}
		}

	public static void main(String args[]) {
		
		f1 = new JFrame();
		f1.setSize(750, 310);
		f1.setTitle("Fastest File Downloader");

		l1 = new JLabel();
		l1.setText("<html><u style='color:navy;'>Faster File Downloader</u><html>");
		l1.setFont(new Font("Times New Roman", Font.BOLD, 25));
		l1.setBounds(250, 10, 350, 30);

		l1.setForeground(Color.red);
		f1.add(l1);

		l2 = new JLabel();
		l2.setText("<html><p style='color:navy; font-style:italic;'>Enter Url to Download file</p></html>");
		l2.setBounds(35, 60, 500, 20);
		l2.setFont(new Font("Times New Roman", Font.BOLD, 18));
		f1.add(l2);

		t1 = new JTextField();
		t1.setText("pase your url");
		t1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		t1.setForeground(Color.red);
		t1.setBounds(35, 85, 550, 30);
		f1.add(t1);
		l3 = new JLabel();
		l3.setText("<html><p style='color:navy; font-style:italic;'>Enter Location where to save files</p></html>");
		l3.setBounds(35, 125, 500, 20);
		l3.setFont(new Font("Times New Roman", Font.BOLD, 18));
		f1.add(l3);

		t2 = new JTextField();
		t2.setText("preferred location to download file");
		t2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		t2.setForeground(Color.red);
		t2.setBounds(35, 150, 450, 30);
		f1.add(t2);

		b1 = new JButton("<html><p style='color:navy; font-style:italic;'>Browse</p></html>");
		b1.setBackground(Color.white);
//b1.setText("Browse");
		b1.setBounds(490, 150, 90, 30);
		f1.add(b1);

		chooser = new JFileChooser();

		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					t2.setText("" + chooser.getSelectedFile());
				} else {
					t2.setText("No Selection ");
				}
			}
		});

		b2 = new JButton("<html><p style='color:navy; font-style:italic;'>Start download</p></html>");
		b2.setBackground(Color.white);
//b1.setText("Browse");
		b2.setBounds(260, 200, 150, 30);
		f1.add(b2);
		
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Working");
				
				 link =t1.getText();
//				  String link = "https://www.google.com/";
				   out = new File(t2.getText());
//				  File out = new File("abc.exe");
				   
				   new Thread(new Downloader(link,out)).start();
//				   l4.setText("Downloaded " + percentDownloaded + " % of file");
				   
			}});
l4 = new JLabel();
l4.setText("");
l4.setBounds(220,240,400,30);
l4.setFont(new Font("Times New Roman", Font.BOLD, 18));
f1.add(l4);

        
        
        
       
		f1.setBackground(Color.WHITE);
		f1.setLayout(null);
		f1.setLocationByPlatform(true);
		f1.setDefaultCloseOperation(f1.EXIT_ON_CLOSE);
		f1.setVisible(true);
		f1.setResizable(false);
	

	}
}