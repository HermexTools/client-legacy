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
	private String saveDir;
    private String ftpAddr;
	private String ftpUser;
	private String ftpPass;
	private String ftpDir;
	private String ftpWebUrl;
    
	private boolean saveEnabled;
	private boolean ftpEnabled;
	private boolean ftpesEnabled;
    private boolean startUpEnabled;
    private boolean acceptAllCertificates;
    
    private int port;
    private int ftpPort;
    private int keyScreen[];
    private int keyCScreen[];
    private int keyFile[];
    private int keyClip[];
	

	private Properties prop;

	public LoadConfig() {
		try {
			this.prop = new Properties();
			System.out.println("[LoadConfig] " + Main.so.getInstallDir().getPath() + "//client.properties");

			if (!new File(Main.so.getInstallDir().getPath() + "//client.properties").exists()) {
				prop.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
			}

			InputStream inputStream = new FileInputStream(Main.so.getInstallDir().getPath() + "//client.properties");
			prop.load(inputStream);
			inputStream.close();

			boolean correct_config = false;

			// Server address
			if ((this.ip = prop.getProperty("server_address")) == null || prop.getProperty("server_address").isEmpty()) {
				prop.setProperty("server_address", "localhost");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default server_address");
			}

			// Socket password
			if ((this.pass = prop.getProperty("password")) == null || prop.getProperty("password").isEmpty()) {
				prop.setProperty("password", "pass");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default password");
			}

			// FTP enabled
			if (prop.getProperty("ftp_enabled") == null || prop.getProperty("ftp_enabled").isEmpty()) {
				prop.setProperty("ftp_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_enabled");
			} else {
				this.ftpEnabled = Boolean.valueOf(prop.getProperty("ftp_enabled"));
			}

			// Socket port
			if (prop.getProperty("port") == null || prop.getProperty("port").isEmpty()) {
				prop.setProperty("port", "4030");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default port");
			} else {
				this.port = Integer.parseInt(prop.getProperty("port"));
			}

			// FTP address
			if ((this.ftpAddr = prop.getProperty("ftp_address")) == null || prop.getProperty("ftp_address").isEmpty()) {
				prop.setProperty("ftp_address", "ftp.mydomain.com");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_address");
			}

			// FTP user
			if ((this.ftpUser = prop.getProperty("ftp_user")) == null || prop.getProperty("ftp_user").isEmpty()) {
				prop.setProperty("ftp_user", "user");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_user");
			}

			// FTP password
			if ((this.ftpPass = prop.getProperty("ftp_password")) == null || prop.getProperty("ftp_password").isEmpty()) {
				prop.setProperty("ftp_password", "pass");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_password");
			}

			// FTP port
			if (prop.getProperty("ftp_port") == null || prop.getProperty("ftp_port").isEmpty()) {
				prop.setProperty("ftp_port", "21");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_port");
			} else {
				this.ftpPort = Integer.parseInt(prop.getProperty("ftp_port"));
			}

			// FTP directory
			if ((this.ftpDir = prop.getProperty("ftp_directory")) == null
					|| prop.getProperty("ftp_directory").isEmpty()) {
				prop.setProperty("ftp_directory", "subFolder/anotherFolder");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_directory");
			}

			// FTP weburl
			if ((this.ftpWebUrl = prop.getProperty("ftp_weburl")) == null || prop.getProperty("ftp_weburl").isEmpty()) {
				prop.setProperty("ftp_weburl", "http://mydomain.com/");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftp_weburl");
			}

			// Save enabled
			if (prop.getProperty("save_enabled") == null || prop.getProperty("save_enabled").isEmpty()) {
				prop.setProperty("save_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default save_enabled");
			} else {
				this.saveEnabled = Boolean.valueOf(prop.getProperty("save_enabled"));
			}

			// Save directory
			if ((this.saveDir = prop.getProperty("save_dir")) == null || prop.getProperty("save_dir").isEmpty()) {
				prop.setProperty("save_dir", ".");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default save_dir");
			}

			// FTPES enabled
			if (prop.getProperty("ftpes_enabled") == null || prop.getProperty("ftpes_enabled").isEmpty()) {
				prop.setProperty("ftpes_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default ftpes_enabled");
			} else {
				this.ftpesEnabled = Boolean.valueOf(prop.getProperty("ftpes_enabled"));
			}
            
            // StartUp enabled
			if (prop.getProperty("open_at_startup_enabled") == null || prop.getProperty("open_at_startup_enabled").isEmpty()) {
				prop.setProperty("open_at_startup_enabled", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default open_at_startup_enabled");
			} else {
				this.startUpEnabled = Boolean.valueOf(prop.getProperty("open_at_startup_enabled"));
			}
            
            // accept all certificates
            if (prop.getProperty("accept_all_certificates") == null || prop.getProperty("accept_all_certificates").isEmpty()) {
				prop.setProperty("accept_all_certificates", "false");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default accept_all_certificates");
			} else {
				this.acceptAllCertificates = Boolean.valueOf(prop.getProperty("accept_all_certificates"));
			}
            
            // keys for partial screen
            if (prop.getProperty("key_screen") == null || prop.getProperty("key_screen").isEmpty()) {
				prop.setProperty("key_screen", "56:2");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_screen");
			} else {
                String[] tmp = prop.getProperty("key_screen").split(":");
                this.keyScreen = new int[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    this.keyScreen[i]=Integer.parseInt(tmp[i]);
                } 
            }
            
            // keys for partial screen
            if (prop.getProperty("key_cscreen") == null || prop.getProperty("key_cscreen").isEmpty()) {
				prop.setProperty("key_cscreen", "56:3");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_cscreen");
			} else {
                String[] tmp = prop.getProperty("key_cscreen").split(":");
                this.keyCScreen = new int[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    this.keyCScreen[i]=Integer.parseInt(tmp[i]);
                } 
            }
            
            // keys for files
            if (prop.getProperty("key_file") == null || prop.getProperty("key_file").isEmpty()) {
				prop.setProperty("key_file", "56:4");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_file");
			} else {
                String[] tmp = prop.getProperty("key_file").split(":");
                this.keyFile = new int[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    this.keyFile[i]=Integer.parseInt(tmp[i]);
                } 
            }
            
            // keys for clipboard
            if (prop.getProperty("key_clipboard") == null || prop.getProperty("key_clipboard").isEmpty()) {
				prop.setProperty("key_clipboard", "56:5");
				correct_config = true;
				System.out.println("[LoadConfig] Setting default key_clipboard");
			} else {
                String[] tmp = prop.getProperty("key_clipboard").split(":");
                this.keyClip = new int[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    this.keyClip[i]=Integer.parseInt(tmp[i]);
                } 
            }

			if (correct_config){
				prop.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);
            }

		} catch (Exception ex) {
			ex.printStackTrace();
			new NotificationDialog().show("Config error", "Error during the config loading!");
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}

	}

	public void storeNewConfig(String ftpWeburl, String ftpDir, String ftport, String ftpPass, String ftpUser,
			String ftpAddr, String ftpEnabled, String srvPass, String srvPort, String srvAddr, String saveEnabled,
			String saveDir, String startUp, String ftpes, String allCertificates ) {
		try {
			prop.setProperty("ftp_weburl", ftpWeburl);
			prop.setProperty("ftp_directory", ftpDir);
			prop.setProperty("ftp_port", ftport);
			prop.setProperty("ftp_password", ftpPass);
			prop.setProperty("ftp_user", ftpUser);
			prop.setProperty("ftp_address", ftpAddr);
			prop.setProperty("ftp_enabled", ftpEnabled);
            prop.setProperty("ftpes_enabled", ftpes);
			prop.setProperty("password", srvPass);
			prop.setProperty("port", srvPort);
			prop.setProperty("server_address", srvAddr);
			prop.setProperty("save_enabled", saveEnabled);
			prop.setProperty("save_dir", saveDir);
            prop.setProperty("open_at_startup_enabled",startUp);
            prop.setProperty("accept_all_certificates", allCertificates);
			prop.store(new FileOutputStream(Main.so.getInstallDir().getPath() + "//client.properties"), null);

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
            this.startUpEnabled = Boolean.valueOf(prop.getProperty("open_at_startup_enabled"));
            this.ftpesEnabled = Boolean.valueOf(prop.getProperty("ftpes_enabled"));
            this.acceptAllCertificates = Boolean.valueOf(prop.getProperty("accept_all_certificates"));
            
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

    public boolean isFtpesEnabled() {
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

    public int[] getKeyClip() {
        return keyClip;
    }
    
    

}
