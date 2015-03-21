import java.awt.AWTException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author Sergio
 */

public class Main {
    
    public static void main(String[] args) throws InterruptedException,
            InvocationTargetException, AWTException, IOException,
            UnsupportedAudioFileException, LineUnavailableException {
        
        final LoadConfig l = new LoadConfig();
        final SystemTrayMenu st = new SystemTrayMenu(l.getIp(), l.getPass());
        
        NativeKeyListener gkl = new NativeKeyListener() {
            
            Boolean altPressed = false;
            
            @Override
            public void nativeKeyPressed(NativeKeyEvent nke) {
                if (nke.getKeyCode() == NativeKeyEvent.VC_ALT_L) {
                    altPressed = true;
                }
                
                if ((altPressed == true)
                        && nke.getKeyCode() == NativeKeyEvent.VC_1) {
                    System.out.println("Alt_l + 1 premuti");
                    
                    try {
                        st.sendPartialScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                }
                
                if ((altPressed == true)
                        && nke.getKeyCode() == NativeKeyEvent.VC_2) {
                    System.out.println("Alt_l + 2 premuti");
                    
                    st.sendCompleteScreen();
                    
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
        
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage()
                .getName());
        logger.setLevel(Level.OFF);
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(Level.OFF);
        }
    }
}
