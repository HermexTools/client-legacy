package it.ksuploader.main;


import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
import it.ksuploader.utils.MyKeyListener;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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