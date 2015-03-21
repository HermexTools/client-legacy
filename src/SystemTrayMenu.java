import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 *
 * @author Sergio
 */
public class SystemTrayMenu{
    private ScreenshotUploader s;
    private PartialScreen cp;
    private CompleteScreen c;
    private Clipboard clpbrd;
    private StringSelection stringSelection;
    private TrayIcon trayIcon;
    private SystemTray st;
    private JPopupMenu p;
    private Clip clip;
    
    private String ip;
    private String id;
    private String pass;
    
    
    public SystemTrayMenu(String ip,String id,String pass)
            throws AWTException, IOException, UnsupportedAudioFileException,
            LineUnavailableException {
        
        this.ip=ip;
        this.id=id;
        this.pass=pass;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println(ex.toString());
        }
        if (SystemTray.isSupported()) {
            clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            st = SystemTray.getSystemTray();
            
            trayIcon = new TrayIcon(ImageIO.read(getClass()
                    .getResource("/res/icona.jpg")), "ownPuush");
            trayIcon.setImageAutoSize(true);
            
            p = new JPopupMenu();
            JMenuItem catturaArea = new JMenuItem("Cattura area");
            JMenuItem catturaDesktop = new JMenuItem("Cattura Desktop");
            JMenuItem esci = new JMenuItem("Esci");
            p.add(catturaArea);
            p.add(catturaDesktop);
            p.add(esci);
            
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(
                    "/res/complete.wav")));
            
            catturaArea.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {  
                        sendPartialScreen();
                    } catch (IOException ex) {
                        System.err.println(ex.toString());
                    }
                }
            });
            
            catturaDesktop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendCompleteScreen();
                }
            });
            
            esci.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    System.exit(0);
                }
            });
            
            MouseAdapter mouseAdapter = new MouseAdapter() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 3) {
                        p.show(e.getComponent(), e.getX(), e.getY() - 70);
                    }
                }
            };
            
            trayIcon.addMouseListener(mouseAdapter);
            st.add(trayIcon);
        }
    }
    
    public void sendPartialScreen() throws IOException{
        p.setVisible(false);
        cp = new PartialScreen();
        try {
            s = new ScreenshotUploader(cp.getSelection(), ip);
            s.send(id, pass);
            trayIcon.displayMessage("Puush Caricato!", s.getLink(),
                    TrayIcon.MessageType.INFO);
            stringSelection = new StringSelection(s.getLink());
            clpbrd.setContents(stringSelection, null);
        } catch (AWTException ex) {
            System.err.println(ex.toString());
        }
        clip.start();
        clip.setFramePosition(0);
        clip.flush(); 
    }
    
    public void sendCompleteScreen(){
        p.setVisible(false);
        try {
            c = new CompleteScreen();
            s = new ScreenshotUploader(c.getImg(), ip);
            s.send(id, pass);
            trayIcon.displayMessage("Puush Caricato!", s.getLink(),
                    TrayIcon.MessageType.INFO);
            stringSelection = new StringSelection(s.getLink());
            clpbrd.setContents(stringSelection, null);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        clip.start();
        clip.setFramePosition(0);
        clip.flush();     
    }
    
}
