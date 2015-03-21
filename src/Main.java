
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
        
        LoadConfig l = new LoadConfig();
        SystemTrayMenu st = new SystemTrayMenu(l.getIp(), l.getId(),l.getPass());
        
        NativeKeyListener gkl=new NativeKeyListener(){
            @Override
            public void nativeKeyPressed(NativeKeyEvent nke) {
                System.out.println(nke.getKeyCode());
            }
            
            @Override
            public void nativeKeyReleased(NativeKeyEvent nke) {
            }
            
            @Override
            public void nativeKeyTyped(NativeKeyEvent nke) {
            }
            
        };
        
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
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
