package downloader;
import java.io.*;
import java.net.*;

class Download extends downloadui implements Runnable{
	
	String link;
	File out;
	
	public Download(String link,File out) {
		this.link=link;
		this.out=out;
		}
	public void run()
	{
		try
		{
			URL url=new URL(link);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			double filesize = (double)http.getContentLengthLong();
			
			BufferedInputStream in = new BufferedInputStream(http.getInputStream());
			FileOutputStream fos = new FileOutputStream(this.out);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte[] buffer = new byte[1024];
			double downloaded = 0.00;
			int read=0;
			double percentdownloaded = 0.00;
			while((read=in.read(buffer,0,1024))>=0) {
			bout.write(buffer,0,read);	
			downloaded+=read;
			percentdownloaded = (downloaded*100)/filesize;
			//String percent = String.format("%4F", percentdownloaded);
			l1.setText("Downloaded " + percentdownloaded + " % of file");
			}
			bout.close();
			in.close();
			l2.setText("Download Completed");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main(String args[])
	{
	   String link =t1.getText();
	   File out = new File(t2.getText());
	   new Thread(new Download(link,out)).start();
	   
	}
	
	

}

