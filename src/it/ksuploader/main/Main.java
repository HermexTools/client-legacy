package it.ksuploader.main;

import it.ksuploader.utils.Environment;
import it.ksuploader.utils.LoadConfig;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Main {

	public static Environment so = new Environment();
    public static HashSet<Integer> keyHashScreen = new HashSet<>();
    public static HashSet<Integer> keyHashCScreen = new HashSet<>();
    public static HashSet<Integer> keyHashFile = new HashSet<>();
    public static HashSet<Integer> keyHashClipboard = new HashSet<>();
	public static LoadConfig config = new LoadConfig();
	public static PrintWriter log;

	public static void main(String[] args) throws FileNotFoundException {
		log = new PrintWriter(Main.so.getInstallDir().getPath() + "//log.txt");

		final SystemTrayMenu st = new SystemTrayMenu();
		final HashSet<Integer> hashKeyGlobal = new HashSet<>();
        
        for (int i : config.getKeyScreen()) {
            keyHashScreen.add(i);
        }
        
        for (int i : config.getKeyCScreen()) {
            keyHashCScreen.add(i);
        }
        for (int i : config.getKeyFile()) {
            keyHashFile.add(i);
        }
        for (int i : config.getKeyClip()) {
            keyHashClipboard.add(i);
        }

		NativeKeyListener gkl = new NativeKeyListener() {

			boolean hash1Ready = false;

			@Override
			public void nativeKeyPressed(NativeKeyEvent nke) {

				if (keyHashScreen.contains(nke.getKeyCode()) || keyHashCScreen.contains(nke.getKeyCode())
						|| keyHashFile.contains(nke.getKeyCode()) || keyHashClipboard.contains(nke.getKeyCode())) {
					hashKeyGlobal.add(nke.getKeyCode());
					hash1Ready = true;
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nke) {

				if ((hash1Ready == true)
						&& (keyHashScreen.contains(nke.getKeyCode()) || keyHashCScreen.contains(nke.getKeyCode())
								|| keyHashFile.contains(nke.getKeyCode()) || keyHashClipboard
									.contains(nke.getKeyCode())

						)) {
					myLog("Combination received");
					hashKeyGlobal.add(nke.getKeyCode());
				}

				if (keyHashScreen.equals(hashKeyGlobal)) {
					System.out.println("Via cattura parziale");
					clearKeyComb();
					st.sendPartialScreen();
				}

				if (keyHashCScreen.equals(hashKeyGlobal)) {
					System.out.println("Via cattura globale");
					clearKeyComb();
					st.sendCompleteScreen();
				}

				if (keyHashFile.equals(hashKeyGlobal)) {
					System.out.println("Via file");
					clearKeyComb();
					st.sendFile();
				}

				if (keyHashClipboard.equals(hashKeyGlobal)) {
					System.out.println("Via clipboard");
					clearKeyComb();
					st.sendClipboard();
				}

				clearKeyComb();
			}

			private void clearKeyComb() {
				System.out.println("Combination cleared");
				hash1Ready = false;
				hashKeyGlobal.clear();
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
