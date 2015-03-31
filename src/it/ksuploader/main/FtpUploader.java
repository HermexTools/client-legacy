package it.ksuploader.main;

import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
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

import javax.imageio.ImageIO;

public class FtpUploader {
	private FTPClient ftpClient;
	private String link;
	private String filePath;
	private LoadConfig config = new LoadConfig();

	// Per gli screen parziali
	public FtpUploader(Rectangle r) {
		try {

			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
			}

			BufferedImage img = new Robot().createScreenCapture(screenRect).getSubimage(r.x, r.y, r.width, r.height);

			String fileName = System.currentTimeMillis() / 1000 + "" + ((int) (Math.random() * 999));
			File toWrite = new File(new Environment().getTempDir() + "/" + fileName + ".png");
			ImageIO.write(img, "png", toWrite);

			this.filePath = toWrite.getPath();

		} catch (AWTException | IOException ex) {
			ex.printStackTrace();
		}
	}

	// Per gli screen completi
	public FtpUploader(BufferedImage bi) {

		try {

			BufferedImage img = bi;

			String fileName = System.currentTimeMillis() / 1000 + "" + ((int) (Math.random() * 999));
			File toWrite = new File(new Environment().getTempDir() + "/" + fileName + ".png");
			ImageIO.write(img, "png", toWrite);

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

		try {
			ftpClient.connect(config.getFtpAddr(), config.getFtpPort());
			System.out.println("[FtpUploader] Connected to the ftp server");
			ftpClient.login(config.getFtpUser(), config.getFtpPass());
			ftpClient.changeDirectory(config.getFtpDir());
			ftpClient.upload(new File(filePath));
			System.out.println("[FtpUploader] File uploaded");
			this.link = config.getFtpWebUrl() + "/" + new File(filePath).getName();
			System.out.println("[FtpUploader] Returning url: " + this.link);
			ftpClient.disconnect(true);
			System.out.println("[FtpUploader] Disconnected");
			return true;
		} catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
				| FTPDataTransferException | FTPAbortedException e) {
			e.printStackTrace();
		}

		return false;

	}

	public String getLink() {
		return link;
	}
}
