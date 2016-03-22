package it.ksuploader.main;

import it.ksuploader.dialogs.PopupDialog;
import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
import it.ksuploader.utils.MyKeyListener;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
	public static Environment so = new Environment();
	public static PopupDialog dialog = new PopupDialog();
	public static LoadConfig config = new LoadConfig();
	public static MyKeyListener keyListener;
	private static Logger log = Logger.getLogger("KSULog");
	public static SystemTrayMenu st;

	public static void main(String[] args) throws IOException, URISyntaxException {
        FileHandler fh = new FileHandler(so.getInstallDir().getPath() + "//KSULog.txt");
        fh.setFormatter(new SimpleFormatter());
        log.addHandler(fh);
        log.setUseParentHandlers(false);
		startUpCheck();
		keyListener = new MyKeyListener();
		SwingUtilities.invokeLater(() -> st = new SystemTrayMenu());

	}

	private static void startUpChecker(String name, String where, String target, String icon) {
		try {
			FileWriter fw = new FileWriter(where + "\\" + name);
			fw.write("[InternetShortcut]\n");
			fw.write("URL=file://" + target + "\n");
			fw.write("IDList=\n");
			fw.write("HotKey=0\n");
			if (!icon.equals("")) {
				fw.write("IconFile=" + icon + "\n");
			}
			fw.flush();
			fw.close();
			myLog("[Main] Shortcut created");
		} catch (IOException e){
			e.printStackTrace();
			myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
	}

	public static void startUpCheck() {
		myLog("[Main] removing shortcut: " + new File(Main.so.getStartUpFolder(), "KSUploader_autostart.url").delete());
		if (config.isStartUpEnabled()) {
			startUpChecker("KSUploader_autostart.url", Main.so.getStartUpFolder(), Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"");
		}
	}

	public static void myLog(String s) {
        System.out.println(s);
        log.info(s);
	}

	public static void myErr(String s){
		log.severe(s);
	}

}