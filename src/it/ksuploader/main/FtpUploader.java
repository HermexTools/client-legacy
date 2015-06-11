package it.ksuploader.main;

import it.sauronsoftware.ftp4j.*;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class FtpUploader extends FTPClient implements Observer{
	private String link;
	private String filePath;

	// Per gli screen parziali
	public FtpUploader(Rectangle r) {
		try {

			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
			}

			BufferedImage img = new Robot().createScreenCapture(screenRect).getSubimage(r.x, r.y, r.width, r.height);

			String fileName = System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999);
			File toWrite = new File(Main.so.getTempDir() + "/" + fileName + ".png");
			ImageIO.write(img, "png", toWrite);

			if (Main.config.isSaveEnabled()) {
				ImageIO.write(img, "png", new File(Main.config.getSaveDir() + "/" + System.currentTimeMillis() / 1000
						+ fileName + ".png"));
				Main.myLog("[Uploader] Screen saved");
			}

			this.filePath = toWrite.getPath();

		} catch (AWTException | IOException ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
	}

	// Per gli screen completi
	public FtpUploader(BufferedImage bi) {
		try {

			String fileName = System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999);
			File toWrite = new File(Main.so.getTempDir() + "/" + fileName + ".png");
			ImageIO.write(bi, "png", toWrite);

			if (Main.config.isSaveEnabled()) {
				ImageIO.write(bi, "png", new File(Main.config.getSaveDir() + "/" + System.currentTimeMillis() / 1000 + fileName + ".png"));
				Main.myLog("[Uploader] Screen saved");
			}

			this.filePath = toWrite.getPath();
		} catch (IOException ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
	}

	// Per i file
	public FtpUploader(String filePath) {
		this.filePath = filePath;
	}

	public boolean send() {
		// ftpClient = new FTPClient();
        Main.progressDialog.addObserver(this);

		Main.myLog("[FtpUploader] FtpesEnabled: " + Main.config.getFtpesEnabled());
		if (Main.config.getFtpesEnabled()) {
			try {

				// Setting up tls connection
				Main.myLog("[FtpUploader] AcceptAllCertificates: " + Main.config.getAcceptAllCertificates());
				if (Main.config.getAcceptAllCertificates()) {

					TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						@Override
						public void checkClientTrusted(X509Certificate[] certs, String authType) {
						}

						@Override
						public void checkServerTrusted(X509Certificate[] certs, String authType) {
						}
					} };

					SSLContext sslContext;

					sslContext = SSLContext.getInstance("TLS");
					sslContext.init(null, trustManager, new SecureRandom());

					SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
					this.setSSLSocketFactory(sslSocketFactory);
				} else {
					this.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
				}

				Main.myLog("[FtpUploader] SetSecurity: SECURITY_FTPES");
				this.setSecurity(FTPClient.SECURITY_FTPES);

			} catch (NoSuchAlgorithmException | KeyManagementException e) {

				e.printStackTrace();
				Main.dialog.show("Connection error", "Unable to connect to the server via ftpes");
				Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
				return false;
			}
		}

		// Connection
		try {
			this.connect(Main.config.getFtpAddr(), Main.config.getFtpPort());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			Main.dialog.show("Connection error", "Unable to connect to the server");
			Main.myErr(Arrays.toString(e1.getStackTrace()).replace(",", "\n"));
			return false;
		}
		Main.myLog("[FtpUploader] Connected to the ftp server");

		// Login
		try {
			this.login(Main.config.getFtpUser(), Main.config.getFtpPass());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			Main.myErr(Arrays.toString(e1.getStackTrace()).replace(",", "\n"));
			Main.dialog.show("Login error", "Unable to login to the server");
			return false;
		}

		// Change directory
		try {
			this.changeDirectory(Main.config.getFtpDir());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			Main.myErr(Arrays.toString(e1.getStackTrace()).replace(",", "\n"));
			Main.dialog.show("Error", "Unable to change directory");
			return false;
		}

		// Upload
		try {
			this.upload(new File(filePath), new MyTransferListener());
			Main.myLog("[FtpUploader] File uploaded");
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException | FTPAbortedException e1) {
			e1.printStackTrace();
			Main.myErr(Arrays.toString(e1.getStackTrace()).replace(",", "\n"));
			Main.dialog.show("Upload aborted", "Upload was stopped!");
			return false;
		}

		// Link return
		try {
			this.link = Main.config.getFtpWebUrl() + new File(filePath).getName();
			Main.myLog("[FtpUploader] Returning url: " + this.link);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
			return false;
		}

		// Clean and Disconnect
		try {
			new File(filePath).delete();
			this.disconnect(true);
			Main.myLog("[FtpUploader] Disconnected");
			return true;
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
			Main.dialog.show("Disconnection error", "Error during the disconnection");
			return false;
		}

	}

	public String getLink() {
		return link;
	}

	@Override
	public void update(Observable o, Object arg) {
		stopUpload();
	}

	private class MyTransferListener implements FTPDataTransferListener {

		long tot_trasf = 0; // in bytes

		public void started() {
		}

		public void transferred(int length) {

			Main.progressDialog.setMessage("Uploading...");
			//Main.progressDialog.setUploader(this);
			int percentage_sent = (int) ((100 * (tot_trasf += length)) / new File(filePath).length());
			Main.myLog("[FtpUploader] Sent: " + percentage_sent + "%");
			Main.progressDialog.set(percentage_sent);
		}

		public void completed() {
			Main.progressDialog.destroy();
		}

		public void aborted() {
			Main.myLog("Connessione ftp abortita");
		}

		public void failed() {
		}

	}

	public void stopUpload() {
		try {
			this.abortCurrentDataTransfer(true);
			this.disconnect(true);
			Main.myLog("Mi disconnetto dallo spazio ftp");
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
	}

}
