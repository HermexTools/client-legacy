package it.ksuploader.main;


import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
import it.ksuploader.utils.MyKeyListener;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jimmc.jshortcut.JShellLink;

public class Main {
    public static Environment so = new Environment();
    public static LoadConfig config = new LoadConfig();
    public static MyKeyListener keyListener;
    public static PrintWriter log;
    public static SystemTrayMenu st;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        log = new PrintWriter(so.getInstallDir().getPath() + "//log.txt");
        keyListener = new MyKeyListener();
        st = new SystemTrayMenu();
        startUpCheck(config.isStartUpEnabled());
	}
    
    public static void startUpCheck(boolean active) {
        myLog("[Main] removing shortcut"+new File(Main.so.getStartUpFolder(),"KSUploader.lnk").delete());
        if(active){
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
            myLog("[Main] removing shortcut"+new File(Main.so.getStartUpFolder(),"KSUploader.lnk").delete());
        }
        
    }

	public static void myLog(String s) {
		System.out.println(s);
		log.println(s);
		log.flush();
	}

	public static void myErr(String s) {
		log.println(s);
		log.flush();
	}

}