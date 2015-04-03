package it.ksuploader.main;

import it.ksuploader.dialogs.NotificationDialog;
import it.ksuploader.utils.Environment;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class FtpUploader {
	private static FTPClient ftpClient;
	private String link;
	private String filePath;
	private NotificationDialog notificationDialog;

	// Per gli screen parziali
	public FtpUploader(Rectangle r) {
		try {

			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
			}

			BufferedImage img = new Robot().createScreenCapture(screenRect).getSubimage(r.x, r.y, r.width, r.height);

			String fileName = System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999);
			File toWrite = new File(new Environment().getTempDir() + "/" + fileName + ".png");
			ImageIO.write(img, "png", toWrite);
            
			if (Main.config.isSaveEnabled()){
				ImageIO.write(img, "png", new File(Main.config.getSaveDir()+"/"+System.currentTimeMillis() / 1000 + "" + fileName + ".png"));
                Main.myLog("[Uploader] Screen saved");
            }

			this.filePath = toWrite.getPath();

		} catch (AWTException | IOException ex) {
			ex.printStackTrace();
		}
	}

	// Per gli screen completi
	public FtpUploader(BufferedImage bi) {

		try {

			BufferedImage img = bi;

			String fileName = System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999);
			File toWrite = new File(new Environment().getTempDir() + "/" + fileName + ".png");
			ImageIO.write(img, "png", toWrite);
            
			if (Main.config.isSaveEnabled()){
				ImageIO.write(img, "png", new File(Main.config.getSaveDir()+"/"+System.currentTimeMillis() / 1000 + "" + fileName + ".png"));
                Main.myLog("[Uploader] Screen saved");
            }

			this.filePath = toWrite.getPath();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Per i file
	public FtpUploader(String filePath) {

		this.filePath = filePath;
	}

	public boolean send(String type) {

		ftpClient = new FTPClient();
		notificationDialog = new NotificationDialog();

		try {
			ftpClient.connect(Main.config.getFtpAddr(), Main.config.getFtpPort());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			notificationDialog.show("Connection error", "Unable to connect to the server");
			return false;
		}
		Main.myLog("[FtpUploader] Connected to the ftp server");
		try {
			ftpClient.login(Main.config.getFtpUser(), Main.config.getFtpPass());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			notificationDialog.show("Login error", "Unable to login to the server");
			return false;
		}

		try {
			ftpClient.changeDirectory(Main.config.getFtpDir());
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e1) {
			e1.printStackTrace();
			notificationDialog.show("Error", "Unable to change directory");
			return false;
		}

		try {
			ftpClient.upload(new File(filePath));
			Main.myLog("[FtpUploader] File uploaded");
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
				| FTPDataTransferException | FTPAbortedException e1) {
			e1.printStackTrace();
			notificationDialog.show("Upload error", "Error during the file upload");
			return false;
		}

		try {
			this.link = Main.config.getFtpWebUrl() + new File(filePath).getName();
			Main.myLog("[FtpUploader] Returning url: " + this.link);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}

		try {
			ftpClient.disconnect(true);
			Main.myLog("[FtpUploader] Disconnected");
			return true;
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
			e.printStackTrace();
			notificationDialog.show("Disconnection error", "Error during the disconnection");
			return false;
		}

	}

	public String getLink() {
		return link;
	}
}
