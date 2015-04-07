package it.ksuploader.utils;

import java.io.File;
import java.util.Locale;

public class Environment {

	private static OS os;

	public enum OS {
		LINUX, WINDOWS, OSX, UNKNOWN
	}

	public Environment() {
		os = getEnvironment();
	}

	private static OS getEnvironment() {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

		if (osName.contains("linux"))
			return os = OS.LINUX;
		else if (osName.contains("mac"))
			return os = OS.OSX;
		else if (osName.contains("windows"))
			return os = OS.WINDOWS;

		return OS.UNKNOWN;
	}

	public String getTempDir() {

		switch (os) {
		case LINUX:
		case WINDOWS:
		case OSX:
			return System.getProperty("java.io.tmpdir");
		default:
			return "UNKNOWN";
		}
	}

	public File getInstallDir() {
		final String appData = System.getenv("AppData");
		final String userHome = System.getProperty("user.home");
		final String dirName = ".ksuploader";
		File f;
		switch (os) {
		case LINUX:
			f = new File(userHome, dirName);
			f.mkdir();
			return f;
		case WINDOWS:
			f = new File(appData, dirName);
			f.mkdir();
			return f;
		case OSX:
			f = new File(System.getProperty("user.home"), "Library/Application Support/" + dirName);
			f.mkdir();
			return f;
		default:
			return null;

		}
	}
    
    public String getStartUpFolder(){
        String f = null;
        switch (os) {
		case LINUX:
			f = "~/.config/autostart";
			return f;
		case WINDOWS:
            f = System.getenv("Appdata")+"\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
			return f;
		case OSX:

			return f;
		default:
			return null;

		}
        
    }

}
