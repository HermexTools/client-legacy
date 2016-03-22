package it.ksuploader.utils;


import it.ksuploader.main.Main;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

@SuppressWarnings("serial")
public class LoadConfig extends Properties {

	private String ip, pass, saveDir, ftpAddr, ftpUser, ftpPass, ftpDir, ftpWebUrl, tmp[];
	private boolean saveEnabled, ftpEnabled, ftpesEnabled, startUpEnabled, acceptAllCertificates;
	private int port, ftpPort, keyScreen[], keyCScreen[], keyFile[], keyClip[];

	public LoadConfig() {
		try {
			System.out.println("[LoadConfig] " + Main.so.getInstallDir().getPath() + "/client.properties");

			if (!new File(Main.so.getInstallDir().getPath(), "client.properties").exists()) {
				this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "/client.properties"), null);
			}

			InputStream inputStream = new FileInputStream(Main.so.getInstallDir().getPath() + "//client.properties");
			this.load(inputStream);
			inputStream.close();

			boolean correct_config = false;

			// Server address
			if (this.getProperty("server_address") == null || this.getProperty("server_address").isEmpty()) {
				this.setProperty("server_address", "localhost");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default server_address");
			}
			this.ip = this.getProperty("server_address");

			// Socket password
			if (this.getProperty("password") == null || this.getProperty("password").isEmpty()) {
				this.setProperty("password", "pass");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default password");
			}
			this.pass = this.getProperty("password");

			// FTP enabled
			if (this.getProperty("ftp_enabled") == null || this.getProperty("ftp_enabled").isEmpty()) {
				this.setProperty("ftp_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_enabled");
			}
			this.ftpEnabled = Boolean.valueOf(this.getProperty("ftp_enabled"));

			// Socket port
			if (this.getProperty("port") == null || this.getProperty("port").isEmpty()) {
				this.setProperty("port", "4030");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default port");
			}
			this.port = Integer.parseInt(this.getProperty("port"));

			// FTP address
			if (this.getProperty("ftp_address") == null || this.getProperty("ftp_address").isEmpty()) {
				this.setProperty("ftp_address", "ftp.mydomain.com");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_address");
			}
			this.ftpAddr = this.getProperty("ftp_address");

			// FTP user
			if (this.getProperty("ftp_user") == null || this.getProperty("ftp_user").isEmpty()) {
				this.setProperty("ftp_user", "user");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_user");
			}
			this.ftpUser = this.getProperty("ftp_user");

			// FTP password
			if (this.getProperty("ftp_password") == null || this.getProperty("ftp_password").isEmpty()) {
				this.setProperty("ftp_password", "pass");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_password");
			}
			this.ftpPass = this.getProperty("ftp_password");

			// FTP port
			if (this.getProperty("ftp_port") == null || this.getProperty("ftp_port").isEmpty()) {
				this.setProperty("ftp_port", "21");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_port");
			}
			this.ftpPort = Integer.parseInt(this.getProperty("ftp_port"));

			// FTP directory
			if (this.getProperty("ftp_directory") == null || this.getProperty("ftp_directory").isEmpty()) {
				this.setProperty("ftp_directory", "subFolder/anotherFolder");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_directory");
			}
			this.ftpDir = this.getProperty("ftp_directory");

			// FTP weburl
			if (this.getProperty("ftp_weburl") == null || this.getProperty("ftp_weburl").isEmpty()) {
				this.setProperty("ftp_weburl", "http://mydomain.com/");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_weburl");
			}
			this.ftpWebUrl = this.getProperty("ftp_weburl");

			// Save enabled
			if (this.getProperty("save_enabled") == null || this.getProperty("save_enabled").isEmpty()) {
				this.setProperty("save_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default save_enabled");
			}
			this.saveEnabled = Boolean.valueOf(this.getProperty("save_enabled"));

			// Save directory
			if (this.getProperty("save_dir") == null || this.getProperty("save_dir").isEmpty()) {
				this.setProperty("save_dir", ".");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default save_dir");
			}
			this.saveDir = this.getProperty("save_dir");

			// FTPES enabled
			if (this.getProperty("ftpes_enabled") == null || this.getProperty("ftpes_enabled").isEmpty()) {
				this.setProperty("ftpes_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftpes_enabled");
			}
			this.ftpesEnabled = Boolean.valueOf(this.getProperty("ftpes_enabled"));

			// StartUp enabled
			if (this.getProperty("open_at_startup_enabled") == null
					|| this.getProperty("open_at_startup_enabled").isEmpty()) {
				this.setProperty("open_at_startup_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default open_at_startup_enabled");
			}
			this.startUpEnabled = Boolean.valueOf(this.getProperty("open_at_startup_enabled"));

			// accept all certificates
			if (this.getProperty("accept_all_certificates") == null
					|| this.getProperty("accept_all_certificates").isEmpty()) {
				this.setProperty("accept_all_certificates", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default accept_all_certificates");
			}
			this.acceptAllCertificates = Boolean.valueOf(this.getProperty("accept_all_certificates"));

			// Keys for partial screen
			if (this.getProperty("key_screen") == null || this.getProperty("key_screen").isEmpty()) {
				this.setProperty("key_screen", "56+2");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_screen");
			}
			tmp = null;
			tmp = this.getProperty("key_screen").split("[+]");
			this.keyScreen = new int[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				this.keyScreen[i] = Integer.parseInt(tmp[i]);
			}

			// Keys for complete screen
			if (this.getProperty("key_cscreen") == null || this.getProperty("key_cscreen").isEmpty()) {
				this.setProperty("key_cscreen", "56+3");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_cscreen");
			}
			tmp = null;
			tmp = this.getProperty("key_cscreen").split("[+]");
			this.keyCScreen = new int[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				this.keyCScreen[i] = Integer.parseInt(tmp[i]);
			}

			// Keys for files
			if (this.getProperty("key_file") == null || this.getProperty("key_file").isEmpty()) {
				this.setProperty("key_file", "56+4");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_file");
			}
			tmp = null;
			tmp = this.getProperty("key_file").split("[+]");
			this.keyFile = new int[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				this.keyFile[i] = Integer.parseInt(tmp[i]);
			}

			// keys for clipboard
			if (this.getProperty("key_clipboard") == null || this.getProperty("key_clipboard").isEmpty()) {
				this.setProperty("key_clipboard", "56+5");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_clipboard");
			}
			tmp = null;
			tmp = this.getProperty("key_clipboard").split("[+]");
			this.keyClip = new int[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				this.keyClip[i] = Integer.parseInt(tmp[i]);
			}

			if (correct_config) {
				this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Main.dialog.show("Config error", "Error during the config loading!", false);
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}

	}

	public void storeNewConfig(String ftpWeburl, String ftpDir, String ftport, String ftpPass, String ftpUser,
	                           String ftpAddr, String ftpEnabled, String srvPass, String srvPort, String srvAddr, String saveEnabled,
	                           String saveDir, String startUp, String ftpes, String allCertificates) {
		try {
			this.setProperty("ftp_weburl", ftpWeburl);
			this.setProperty("ftp_directory", ftpDir);
			this.setProperty("ftp_port", ftport);
			this.setProperty("ftp_password", ftpPass);
			this.setProperty("ftp_user", ftpUser);
			this.setProperty("ftp_address", ftpAddr);
			this.setProperty("ftp_enabled", ftpEnabled);
			this.setProperty("ftpes_enabled", ftpes);
			this.setProperty("password", srvPass);
			this.setProperty("port", srvPort);
			this.setProperty("server_address", srvAddr);
			this.setProperty("save_enabled", saveEnabled);
			this.setProperty("save_dir", saveDir);
			this.setProperty("open_at_startup_enabled", startUp);
			this.setProperty("accept_all_certificates", allCertificates);
			this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);

			this.ip = this.getProperty("server_address");
			this.pass = this.getProperty("password");
			this.ftpEnabled = Boolean.valueOf(this.getProperty("ftp_enabled"));
			this.port = Integer.parseInt(this.getProperty("port"));
			this.ftpAddr = this.getProperty("ftp_address");
			this.ftpUser = this.getProperty("ftp_user");
			this.ftpPass = this.getProperty("ftp_password");
			this.ftpPort = Integer.parseInt(this.getProperty("ftp_port"));
			this.ftpDir = this.getProperty("ftp_directory");
			this.ftpWebUrl = this.getProperty("ftp_weburl");
			this.saveEnabled = Boolean.valueOf(this.getProperty("save_enabled"));
			this.saveDir = this.getProperty("save_dir");
			this.startUpEnabled = Boolean.valueOf(this.getProperty("open_at_startup_enabled"));
			this.ftpesEnabled = Boolean.valueOf(this.getProperty("ftpes_enabled"));
			this.acceptAllCertificates = Boolean.valueOf(this.getProperty("accept_all_certificates"));

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

	public boolean getFtpesEnabled() {
		return ftpesEnabled;
	}

	public boolean isStartUpEnabled() {
		return startUpEnabled;
	}

	public boolean getAcceptAllCertificates() {
		return acceptAllCertificates;
	}

	public int[] getKeyScreen() {
		return keyScreen;
	}

	public int[] getKeyCScreen() {
		return keyCScreen;
	}

	public int[] getKeyFile() {
		return keyFile;
	}

	public int[] getKeyClipboard() {
		return keyClip;
	}

	public void setScreenKeys(String comb) {
		this.setProperty("key_screen", comb);
		try {
			this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		tmp = this.getProperty("key_screen").split("[+]");
		this.keyScreen = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			this.keyScreen[i] = Integer.parseInt(tmp[i]);
		}
	}

	public void setCScreenKeys(String comb) {
		this.setProperty("key_cscreen", comb);
		try {
			this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		tmp = this.getProperty("key_cscreen").split("[+]");
		this.keyCScreen = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			this.keyCScreen[i] = Integer.parseInt(tmp[i]);
		}
	}

	public void setFileKeys(String comb) {
		this.setProperty("key_file", comb);
		try {
			this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		tmp = this.getProperty("key_file").split("[+]");
		this.keyFile = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			this.keyFile[i] = Integer.parseInt(tmp[i]);
		}
	}

	public void setClipboardKeys(String comb) {
		this.setProperty("key_clipboard", comb);
		try {
			this.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		tmp = this.getProperty("key_clipboard").split("[+]");
		this.keyClip = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			this.keyClip[i] = Integer.parseInt(tmp[i]);
		}
	}

}
