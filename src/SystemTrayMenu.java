import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SystemTrayMenu {
	private Uploader uploader;
	private PartialScreen partialScreen;
	private CompleteScreen completeScreen;
	private Clipboard clpbrd;
	private StringSelection stringSelection;
	private TrayIcon trayIcon;
	private SystemTray systemTray;
	private PopupMenu popupMenu;
	private Clip clip;

	private String ip;
	private String pass;

	public SystemTrayMenu(String ip, String pass) throws AWTException,
			IOException, UnsupportedAudioFileException,
			LineUnavailableException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}

		this.ip = ip;
		this.pass = pass;

		if (SystemTray.isSupported()) {
			clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			systemTray = SystemTray.getSystemTray();

			trayIcon = new TrayIcon(ImageIO.read(getClass().getResource(
					"/res/icona.jpg")), "ownPuush");
			trayIcon.setImageAutoSize(true);

			popupMenu = new PopupMenu();
			MenuItem catturaArea = new MenuItem("Cattura Area (ALT+1)");
			MenuItem catturaDesktop = new MenuItem("Cattura Desktop (ALT+2)");
			MenuItem caricaFile = new MenuItem("Carica File");
			MenuItem esci = new MenuItem("Esci");

			popupMenu.add(catturaArea);
			popupMenu.add(catturaDesktop);
			popupMenu.addSeparator();
			popupMenu.add(caricaFile);
			popupMenu.addSeparator();
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

			caricaFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						sendFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});

			esci.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SystemTray.getSystemTray().remove(trayIcon);
					System.exit(0);
				}
			});

			trayIcon.setPopupMenu(popupMenu);
			systemTray.add(trayIcon);
		}
	}

	public void sendPartialScreen() throws IOException {
		partialScreen = new PartialScreen();
		try {

			// Se lo screenshot è più piccolo di 5px in h|w
			if (partialScreen.getSelection().width <= 5
					|| partialScreen.getSelection().height <= 5) {

				// Annullo
				trayIcon.displayMessage("Info", "Caricamento annullato :(",
						TrayIcon.MessageType.INFO);
			} else {

				// Altrimenti invio
				uploader = new Uploader(partialScreen.getSelection(), ip);
				uploader.send(pass, "img");
				new NotificationDialog("Screenshot Caricato!",
						uploader.getLink());

				stringSelection = new StringSelection(uploader.getLink());
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
		try {
			completeScreen = new CompleteScreen();
			uploader = new Uploader(completeScreen.getImg(), ip);
			uploader.send(pass, "img");
			new NotificationDialog("Screenshot Caricato!", uploader.getLink());
			stringSelection = new StringSelection(uploader.getLink());
			clpbrd.setContents(stringSelection, null);
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}
		clip.start();
		clip.setFramePosition(0);
		clip.flush();
	}

	public void sendFile() throws IOException {
		System.out.println("Not implemented yet.");

		String fileName = "test.lol";
		new Zipper(fileName).toZip();
		uploader = new Uploader(fileName + ".zip", ip);

		uploader.send(pass, "file");

	}

}
