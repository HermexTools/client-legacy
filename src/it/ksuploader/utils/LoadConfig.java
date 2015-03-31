package it.ksuploader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadConfig {

	private String ip;
	private String pass;
	private int port;
	private boolean ftpEnabled;
	private String ftpAddr;
	private String ftpUser;
	private String ftpPass;
	private int ftpPort;
	private String ftpDir;
	private String ftpWebUrl;

	public LoadConfig() {
		try {
			Properties prop = new Properties();

			if (!new File("client.properties").exists()) {
				prop.setProperty("ftp_weburl", "http://mydomain.com");
				prop.setProperty("ftp_directory", "subFolder/anotherFolder");
				prop.setProperty("ftp_port", "21");
				prop.setProperty("ftp_password", "pass");
				prop.setProperty("ftp_user", "user");
				prop.setProperty("ftp_address", "ftp.mydomain.com");
				prop.setProperty("ftp_enabled", "false");
				prop.setProperty("password", "pass");
				prop.setProperty("port", "4030");
				prop.setProperty("server_address", "localhost");
				prop.store(new FileOutputStream("client.properties"), null);
			}
			InputStream inputStream = new FileInputStream("client.properties");
			prop.load(inputStream);

			this.ip = prop.getProperty("server_addr");
			this.pass = prop.getProperty("password");
			this.ftpEnabled = Boolean.valueOf(prop.getProperty("ftp_enabled"));
			this.port = Integer.parseInt(prop.getProperty("port"));
			this.ftpAddr = prop.getProperty("ftp_address");
			this.ftpUser = prop.getProperty("ftp_user");
			this.ftpPass = prop.getProperty("ftp_password");
			this.ftpPort = Integer.parseInt(prop.getProperty("ftp_port"));
			this.ftpDir = prop.getProperty("ftp_directory");
			this.ftpWebUrl = prop.getProperty("ftp_weburl");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public boolean getFtpEnabled() {
		return ftpEnabled;
	}

	public String getFtpAddr() {
		return ftpAddr;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public String getFtpPass() {
		return ftpPass;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public String getFtpDir() {
		return ftpDir;
	}

	public String getFtpWebUrl() {
		return ftpWebUrl;
	}

	public String getIp() {
		return ip;
	}

	public String getPass() {
		return pass;
	}

	public int getPort() {
		return port;
	}

}
