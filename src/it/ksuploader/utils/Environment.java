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

		switch (os) {
		case LINUX:
			return new File(userHome, dirName);
		case WINDOWS:
			return new File(userHome, "AppData\\Roaming\\" + dirName);
		case OSX:
			return new File(System.getProperty("user.home"), "Library/Application Support/" + dirName);
		default:
			return null;

		}
	}

}
