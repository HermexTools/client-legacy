package it.ksuploader.utils;

import it.ksuploader.dialogs.NotificationDialog;
import it.ksuploader.main.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class LoadConfig {

	private String ip;
	private String pass;
	private int port;
	private boolean saveEnabled;
	private String saveDir;

	private boolean ftpEnabled;
	private String ftpAddr;
	private String ftpUser;
	private String ftpPass;
	private int ftpPort;
	private String ftpDir;
	private String ftpWebUrl;

	private Properties prop;

	public LoadConfig() {
		try {
			this.prop = new Properties();
            System.out.println("[LoadConfig] "+Main.so.getInstallDir().getPath()+"//client.properties");
			if (!new File(Main.so.getInstallDir().getPath()+"//client.properties").exists()) {
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
				prop.setProperty("save_enabled", "false");
				prop.setProperty("save_dir", ".");
				prop.store(new FileOutputStream(Main.so.getInstallDir().getPath()+"//client.properties"), null);
			}
			InputStream inputStream = new FileInputStream(Main.so.getInstallDir().getPath()+"//client.properties");
			prop.load(inputStream);

			this.ip = prop.getProperty("server_address");
			this.pass = prop.getProperty("password");
			this.ftpEnabled = Boolean.valueOf(prop.getProperty("ftp_enabled"));
			this.port = Integer.parseInt(prop.getProperty("port"));
			this.ftpAddr = prop.getProperty("ftp_address");
			this.ftpUser = prop.getProperty("ftp_user");
			this.ftpPass = prop.getProperty("ftp_password");
			this.ftpPort = Integer.parseInt(prop.getProperty("ftp_port"));
			this.ftpDir = prop.getProperty("ftp_directory");
			this.ftpWebUrl = prop.getProperty("ftp_weburl");
			this.saveEnabled = Boolean.valueOf(prop.getProperty("save_enabled"));
			this.saveDir = prop.getProperty("save_dir");

		} catch (Exception ex) {
			ex.printStackTrace();
			new NotificationDialog().show("Config error", "Error during the config loading!");
            Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}

	}

	public void storeNewConfig(String ftpWeburl, String ftpDir, String ftport, String ftpPass, String ftpUser,
			String ftpAddr, String ftpEnabled, String srvPass, String srvPort, String srvAddr, String saveEnabled,
			String saveDir) {
		try {
			prop.setProperty("ftp_weburl", ftpWeburl);
			prop.setProperty("ftp_directory", ftpDir);
			prop.setProperty("ftp_port", ftport);
			prop.setProperty("ftp_password", ftpPass);
			prop.setProperty("ftp_user", ftpUser);
			prop.setProperty("ftp_address", ftpAddr);
			prop.setProperty("ftp_enabled", ftpEnabled);
			prop.setProperty("password", srvPass);
			prop.setProperty("port", srvPort);
			prop.setProperty("server_address", srvAddr);
			prop.setProperty("save_enabled", saveEnabled);
			prop.setProperty("save_dir", saveDir);
			prop.store(new FileOutputStream(Main.so.getInstallDir().getPath()+"//client.properties"), null);
		} catch (IOException ex) {
			ex.printStackTrace();
            Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
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

	public boolean isSaveEnabled() {
		return saveEnabled;
	}

	public String getSaveDir() {
		return saveDir;
	}

}
