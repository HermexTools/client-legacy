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
		final String userHome = System.getProperty("user.home");
		final String dirName = ".ksuploader";
        File f;
		switch (os) {
		case LINUX:
            f = new File(userHome, dirName);
            f.mkdir();
			return f;
		case WINDOWS:
            f = new File(userHome, "AppData\\Roaming\\" + dirName);
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

}
