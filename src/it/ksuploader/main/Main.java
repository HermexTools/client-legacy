package it.ksuploader.main;

import it.ksuploader.dialogs.PopupDialog;
import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
import it.ksuploader.utils.MyKeyListener;
import net.jimmc.jshortcut.JShellLink;

import java.io.File;
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
	public static Logger log = Logger.getLogger("KSULog");
	public static SystemTrayMenu st;

	public static void main(String[] args) throws IOException, URISyntaxException {
        FileHandler fh = new FileHandler(so.getInstallDir().getPath() + "//KSULog.txt");
        fh.setFormatter(new SimpleFormatter());
        log.addHandler(fh);
        log.setUseParentHandlers(false);
		keyListener = new MyKeyListener();
		st = new SystemTrayMenu();
		startUpCheck(config.isStartUpEnabled());
	}

	public static void startUpCheck(boolean active) {
		myLog("[Main] removing shortcut" + new File(Main.so.getStartUpFolder(), "KSUploader.lnk").delete());
		if (active) {
			try {
				JShellLink shortcut = new JShellLink();
				shortcut.setPath(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());
				shortcut.setName("KSUploader_autostart");
				shortcut.setFolder(Main.so.getStartUpFolder());
				shortcut.save();
				myLog("[Main] Shortcut created");
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
				myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
			}
		} else {
			myLog("[Main] removing shortcut" + new File(Main.so.getStartUpFolder(), "KSUploader.lnk").delete());
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