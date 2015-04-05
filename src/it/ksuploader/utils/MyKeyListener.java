package it.ksuploader.utils;

import it.ksuploader.main.Main;
import static it.ksuploader.main.Main.myLog;
import static it.ksuploader.main.Main.st;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author Sergio
 */
public class MyKeyListener {
    public static HashMap<Integer, String> fromKeyToName = new HashMap<>();;
    public HashSet<Integer> keyHashScreen;
    public HashSet<Integer> keyHashCScreen;
    public HashSet<Integer> keyHashFile;
    public HashSet<Integer> keyHashClipboard;
    public HashSet<Integer> hashKeyGlobal;

    public MyKeyListener() {
        
        hashKeyGlobal = new HashSet<>();
        
        fromKeyToName.put(NativeKeyEvent.VC_ALT_L, "ALT-L");
        fromKeyToName.put(NativeKeyEvent.VC_ALT_R, "ALT-R");
        fromKeyToName.put(NativeKeyEvent.VC_SHIFT_L, "SHIFT-L");
        fromKeyToName.put(NativeKeyEvent.VC_SHIFT_R, "SHIFT-R");
        fromKeyToName.put(NativeKeyEvent.VC_CONTROL_L, "CTRL-L");
        fromKeyToName.put(NativeKeyEvent.VC_CONTROL_R, "CTRL-R");
        fromKeyToName.put(NativeKeyEvent.VC_0, "0");
        fromKeyToName.put(NativeKeyEvent.VC_1, "1");
        fromKeyToName.put(NativeKeyEvent.VC_2, "2");
        fromKeyToName.put(NativeKeyEvent.VC_3, "3");
        fromKeyToName.put(NativeKeyEvent.VC_4, "4");
        fromKeyToName.put(NativeKeyEvent.VC_5, "5");
        fromKeyToName.put(NativeKeyEvent.VC_6, "6");
        fromKeyToName.put(NativeKeyEvent.VC_7, "7");
        fromKeyToName.put(NativeKeyEvent.VC_8, "8");
        fromKeyToName.put(NativeKeyEvent.VC_9, "9");
        fromKeyToName.put(NativeKeyEvent.VC_A, "A");
        fromKeyToName.put(NativeKeyEvent.VC_B, "B");
        fromKeyToName.put(NativeKeyEvent.VC_C, "C");
        fromKeyToName.put(NativeKeyEvent.VC_D, "D");
        fromKeyToName.put(NativeKeyEvent.VC_E, "E");
        fromKeyToName.put(NativeKeyEvent.VC_F, "F");
        fromKeyToName.put(NativeKeyEvent.VC_G, "G");
        fromKeyToName.put(NativeKeyEvent.VC_H, "H");
        fromKeyToName.put(NativeKeyEvent.VC_I, "I");
        fromKeyToName.put(NativeKeyEvent.VC_J, "J");
        fromKeyToName.put(NativeKeyEvent.VC_K, "K");
        fromKeyToName.put(NativeKeyEvent.VC_L, "L");
        fromKeyToName.put(NativeKeyEvent.VC_M, "M");
        fromKeyToName.put(NativeKeyEvent.VC_N, "N");
        fromKeyToName.put(NativeKeyEvent.VC_O, "O");
        fromKeyToName.put(NativeKeyEvent.VC_P, "P");
        fromKeyToName.put(NativeKeyEvent.VC_Q, "Q");
        fromKeyToName.put(NativeKeyEvent.VC_R, "R");
        fromKeyToName.put(NativeKeyEvent.VC_S, "S");
        fromKeyToName.put(NativeKeyEvent.VC_T, "T");
        fromKeyToName.put(NativeKeyEvent.VC_U, "U");
        fromKeyToName.put(NativeKeyEvent.VC_V, "V");
        fromKeyToName.put(NativeKeyEvent.VC_W, "W");
        fromKeyToName.put(NativeKeyEvent.VC_X, "X");
        fromKeyToName.put(NativeKeyEvent.VC_Y, "Y");
        fromKeyToName.put(NativeKeyEvent.VC_Z, "Z");
        
        
        
        NativeKeyListener gkl = new NativeKeyListener() {

			boolean hash1Ready = false;

			@Override
			public void nativeKeyPressed(NativeKeyEvent nke) {

				if (keyHashScreen.contains(nke.getKeyCode())
						|| keyHashCScreen.contains(nke.getKeyCode())
						|| keyHashFile.contains(nke.getKeyCode())
						|| keyHashClipboard.contains(nke.getKeyCode())) {
					hashKeyGlobal.add(nke.getKeyCode());
					hash1Ready = true;
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nke) {

				if ((hash1Ready == true)
						&& (keyHashScreen.contains(nke.getKeyCode())
								|| keyHashCScreen.contains(nke.getKeyCode())
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
				// System.out.println("Combination cleared");
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
        loadKeys();
    }
    
    public void loadKeys(){
        keyHashScreen = new HashSet<>();
        keyHashCScreen = new HashSet<>();
        keyHashFile = new HashSet<>();
        keyHashClipboard = new HashSet<>();
        for (int i : Main.config.getKeyScreen()) {
            keyHashScreen.add(i);
        }
        
        for (int i : Main.config.getKeyCScreen()) {
            keyHashCScreen.add(i);
        }
        for (int i : Main.config.getKeyFile()) {
            keyHashFile.add(i);
        }
        for (int i : Main.config.getKeyClipboard()) {
            keyHashClipboard.add(i);
        }
    }
    
    
    
}
