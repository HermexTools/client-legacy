package it.ksuploader.main;

import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Main {
	
    public static Environment so = new Environment();
    public static LoadConfig config = new LoadConfig();
    public static PrintWriter log; 

	public static void main(String[] args) throws FileNotFoundException {
        
        log = new PrintWriter(Main.so.getInstallDir().getPath()+"//log.txt");
		final SystemTrayMenu st = new SystemTrayMenu();

		NativeKeyListener gkl = new NativeKeyListener() {

			boolean altPressed = false;

			@Override
			public void nativeKeyPressed(NativeKeyEvent nke) {
				if (nke.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
					altPressed = true;
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nke) {

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_1) {
					myLog("Alt_l + 1 premuti");

					st.sendPartialScreen();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_2) {
					myLog("Alt_l + 2 premuti");

					st.sendCompleteScreen();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_3) {
					myLog("Alt_l + 3 premuti");

					st.sendFile();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_4) {
					myLog("Alt_l + 4 premuti");

					st.sendClipboard();
				}

				if (nke.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
					altPressed = false;
				}
			}

			@Override
			public void nativeKeyTyped(NativeKeyEvent nke) {
			}

		};

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			myLog("Impossibile inizializzare NativeHook");
			myLog(ex.getMessage());
		}

		GlobalScreen.addNativeKeyListener(gkl);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}
    
    public static void myLog(String s){
        System.out.println(s);
        log.println(s);
    }
}
