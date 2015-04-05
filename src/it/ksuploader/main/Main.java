package it.ksuploader.main;

import it.ksuploader.utils.Constants;

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

	public static void main(String[] args) throws FileNotFoundException {
		new Constants();
		Constants.log = new PrintWriter(Constants.so.getInstallDir().getPath() + "//log.txt");

		final HashSet<Integer> hashKeyGlobal = new HashSet<>();

		for (int i : Constants.config.getKeyScreen()) {
			Constants.keyHashScreen.add(i);
		}

		for (int i : Constants.config.getKeyCScreen()) {
			Constants.keyHashCScreen.add(i);
		}
		for (int i : Constants.config.getKeyFile()) {
			Constants.keyHashFile.add(i);
		}
		for (int i : Constants.config.getKeyClipboard()) {
			Constants.keyHashClipboard.add(i);
		}

		NativeKeyListener gkl = new NativeKeyListener() {

			boolean hash1Ready = false;

			@Override
			public void nativeKeyPressed(NativeKeyEvent nke) {

				if (Constants.keyHashScreen.contains(nke.getKeyCode())
						|| Constants.keyHashCScreen.contains(nke.getKeyCode())
						|| Constants.keyHashFile.contains(nke.getKeyCode())
						|| Constants.keyHashClipboard.contains(nke.getKeyCode())) {
					hashKeyGlobal.add(nke.getKeyCode());
					hash1Ready = true;
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nke) {

				if ((hash1Ready == true)
						&& (Constants.keyHashScreen.contains(nke.getKeyCode())
								|| Constants.keyHashCScreen.contains(nke.getKeyCode())
								|| Constants.keyHashFile.contains(nke.getKeyCode()) || Constants.keyHashClipboard
									.contains(nke.getKeyCode())

						)) {
					myLog("Combination received");
					hashKeyGlobal.add(nke.getKeyCode());
				}

				if (Constants.keyHashScreen.equals(hashKeyGlobal)) {
					System.out.println("Via cattura parziale");
					clearKeyComb();
					Constants.st.sendPartialScreen();
				}

				if (Constants.keyHashCScreen.equals(hashKeyGlobal)) {
					System.out.println("Via cattura globale");
					clearKeyComb();
					Constants.st.sendCompleteScreen();
				}

				if (Constants.keyHashFile.equals(hashKeyGlobal)) {
					System.out.println("Via file");
					clearKeyComb();
					Constants.st.sendFile();
				}

				if (Constants.keyHashClipboard.equals(hashKeyGlobal)) {
					System.out.println("Via clipboard");
					clearKeyComb();
					Constants.st.sendClipboard();
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
	}

	public static void myLog(String s) {
		System.out.println(s);
		Constants.log.println(s);
		Constants.log.flush();
	}

	public static void myErr(String s) {
		Constants.log.println(s);
		Constants.log.flush();
	}

}
