package downloader;
import javax.swing.*;
import java.awt.*;
import java.io.*;


public class downloadui {
	static JFrame f1;
	static JFileChooser fileChooser1;
	static File file1;
	static JPanel p1;
	static JButton b1,b2;
	static JTextField t1,t2;
	static JLabel l1,l2;
	static JProgressBar pb1;
	
	public void ui()
	{
		f1 = new JFrame("JAVA File Downloader");
		f1.setSize(692,203);
		f1.setVisible(true);
		f1.setLayout(null);
		f1.setDefaultCloseOperation(f1.EXIT_ON_CLOSE);
		
		
	}
	public static void main(String args[])
	{
		downloadui d = new downloadui();
		d.ui();
	}
}

