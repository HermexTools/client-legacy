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
import net.jimmc.jshortcut.JShellLink;

public class Main {
    public static Environment so = new Environment();
    public static LoadConfig config = new LoadConfig();
    public static MyKeyListener keyListener;
    public static PrintWriter log;
    public static SystemTrayMenu st;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        log = new PrintWriter(so.getInstallDir().getPath() + "//log.txt");
        keyListener = new MyKeyListener();
        st = new SystemTrayMenu();
        startUpCheck(config.isStartUpEnabled());
	}
    
    public static void startUpCheck(boolean active) {
        if(active){
            try {
                JShellLink shortcut = new JShellLink();
                shortcut.setPath(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                shortcut.setName("KSUploader");
                shortcut.setFolder(Main.so.getStartUpFolder());
                shortcut.save();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
            }
        } else {
            System.out.println(new File(Main.so.getStartUpFolder(),"KSUploader.lnk").delete());
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