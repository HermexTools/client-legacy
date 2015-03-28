import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Main {

	public static void main(String[] args){
                
                LoadConfig config = new LoadConfig();
		final SystemTrayMenu st = new SystemTrayMenu(config.getIp(),config.getPass(),config.getPort());

		NativeKeyListener gkl = new NativeKeyListener() {

			boolean altPressed = false;

			@Override
			public void nativeKeyPressed(NativeKeyEvent nke) {
				if (nke.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
					altPressed = true;
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_1) {
					System.out.println("Alt_l + 1 premuti");

                                        st.sendPartialScreen();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_2) {
					System.out.println("Alt_l + 2 premuti");
                                        
					st.sendCompleteScreen();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_3) {
					System.out.println("Alt_l + 3 premuti");
                                        
                                        st.sendFile();
				}

				if ((altPressed == true) && nke.getKeyCode() == NativeKeyEvent.VC_4) {
					System.out.println("Alt_l + 4 premuti");

					st.sendClipboard();
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nke) {
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
			System.err.println("Impossibile inizializzare NativeHook");
			System.err.println(ex.getMessage());
		}

		GlobalScreen.addNativeKeyListener(gkl);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}
}
