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
public class SystemTrayMenu {
	private ScreenshotUploader screenshotUploader;
	private PartialScreen partialScreen;
	private CompleteScreen completeScreen;
	private Clipboard clpbrd;
	private StringSelection stringSelection;
	private TrayIcon trayIcon;
	private SystemTray systemTray;
	private JPopupMenu popupMenu;
	private Clip clip;

	private String ip;
	private String pass;

	public SystemTrayMenu(String ip, String pass)
			throws AWTException, IOException, UnsupportedAudioFileException,
			LineUnavailableException {

		this.ip = ip;
		this.pass = pass;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}
		if (SystemTray.isSupported()) {
			clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			systemTray = SystemTray.getSystemTray();

			trayIcon = new TrayIcon(ImageIO.read(getClass().getResource(
					"/res/icona.jpg")), "ownPuush");
			trayIcon.setImageAutoSize(true);

			popupMenu = new JPopupMenu();
			JMenuItem catturaArea = new JMenuItem("Cattura area");
			JMenuItem catturaDesktop = new JMenuItem("Cattura Desktop");
			JMenuItem esci = new JMenuItem("Esci");
			popupMenu.add(catturaArea);
			popupMenu.add(catturaDesktop);
			popupMenu.add(esci);

			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource(
					"/res/complete.wav")));

			// Gestione voci menu
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
						popupMenu.show(e.getComponent(), e.getX(),
								e.getY() - 70);
					}
				}
			};

			trayIcon.addMouseListener(mouseAdapter);
			systemTray.add(trayIcon);
		}
	}

	public void sendPartialScreen() throws IOException {
		popupMenu.setVisible(false);
		partialScreen = new PartialScreen();
		try {

			// Se lo screenshot è più piccolo di 5px in h|w
			if (partialScreen.getSelection().width <= 5 || partialScreen.getSelection().height <= 5) {

				// Annullo
				trayIcon.displayMessage("Info", "Caricamento annullato :(",
						TrayIcon.MessageType.INFO);
			} else {

				// Altrimenti invio
				screenshotUploader = new ScreenshotUploader(partialScreen.getSelection(), ip);
				screenshotUploader.send(pass);
				trayIcon.displayMessage("Puush Caricato!", screenshotUploader.getLink(),
						TrayIcon.MessageType.INFO);
				stringSelection = new StringSelection(screenshotUploader.getLink());
				clpbrd.setContents(stringSelection, null);
			}

		} catch (AWTException ex) {
			System.err.println(ex.toString());
		}
		clip.start();
		clip.setFramePosition(0);
		clip.flush();
	}

	public void sendCompleteScreen() {
		popupMenu.setVisible(false);
		try {
			completeScreen = new CompleteScreen();
			screenshotUploader = new ScreenshotUploader(completeScreen.getImg(), ip);
			screenshotUploader.send(pass);
			trayIcon.displayMessage("Puush Caricato!", screenshotUploader.getLink(),
					TrayIcon.MessageType.INFO);
			stringSelection = new StringSelection(screenshotUploader.getLink());
			clpbrd.setContents(stringSelection, null);
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}
		clip.start();
		clip.setFramePosition(0);
		clip.flush();
	}

}
