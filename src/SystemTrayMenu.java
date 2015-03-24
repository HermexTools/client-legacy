import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SystemTrayMenu {
	private Uploader uploader;
	private PartialScreen partialScreen;
	private CompleteScreen completeScreen;
	private Clipboard clpbrd;
	private TrayIcon trayIcon;
	private SystemTray systemTray;
	private PopupMenu popupMenu;
	private Clip clip;
	private MenuItem[] uploads;

	private String ip;
	private String pass;
	private int port;

	public SystemTrayMenu() throws AWTException, IOException, UnsupportedAudioFileException, LineUnavailableException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}

		final LoadConfig loadConfig = new LoadConfig();

		this.ip = loadConfig.getIp();
		this.pass = loadConfig.getPass();
		this.port = loadConfig.getPort();
		this.uploads = new MenuItem[5];

		for (int i = 0; i < uploads.length; i++) {
			uploads[i] = new MenuItem();
		}

		if (SystemTray.isSupported()) {
			clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			systemTray = SystemTray.getSystemTray();

			trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/res/icona.jpg")), "ownPuush");
			trayIcon.setImageAutoSize(true);

			popupMenu = new PopupMenu();
			MenuItem catturaArea = new MenuItem("Cattura Area (ALT+1)");
			MenuItem catturaDesktop = new MenuItem("Cattura Desktop (ALT+2)");
			MenuItem caricaFile = new MenuItem("Carica File (ALT+3)");
                        MenuItem clipboard = new MenuItem("Carica Clipboard (ALT+4)");
			MenuItem esci = new MenuItem("Esci");

			popupMenu.add("Upload Recenti");
			popupMenu.addSeparator();
			popupMenu.addSeparator();
			popupMenu.add(catturaArea);
			popupMenu.add(catturaDesktop);
			popupMenu.addSeparator();
			popupMenu.add(caricaFile);
                        popupMenu.add(clipboard);
			popupMenu.addSeparator();
			popupMenu.add(esci);

			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/res/complete.wav")));

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
                        clipboard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendClipboard();
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
			if (partialScreen.getSelection().width <= 5 || partialScreen.getSelection().height <= 5) {

				// Annullo
				trayIcon.displayMessage("Info", "Caricamento annullato :(", TrayIcon.MessageType.INFO);
			} else {

				// Altrimenti invio
				uploader = new Uploader(partialScreen.getSelection(), ip, port);
				uploader.send(pass, "img");
				new NotificationDialog("Screenshot Caricato!", uploader.getLink());
				history(uploader.getLink());
				clpbrd.setContents(new StringSelection(uploader.getLink()), null);
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
			uploader = new Uploader(completeScreen.getImg(), ip, port);
			uploader.send(pass, "img");
			new NotificationDialog("Screenshot Caricato!", uploader.getLink());
			history(uploader.getLink());
			clpbrd.setContents(new StringSelection(uploader.getLink()), null);
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}
		clip.start();
		clip.setFramePosition(0);
		clip.flush();
	}

	public void sendFile() throws IOException {

		try {
			JFileChooser selFile = new JFileChooser();
			if (selFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				uploader = new Uploader(new Zipper(selFile.getSelectedFile()).toZip(), ip, port);
				uploader.send(pass, "file");
				new NotificationDialog("File Caricato!", uploader.getLink());
				history(uploader.getLink());
				clpbrd.setContents(new StringSelection(uploader.getLink()), null);
                                new File(selFile.getSelectedFile().getName().split("\\.")[0]+".zip").delete();
			}
		} catch (Exception ex) {
			System.err.println(ex.toString());
		}
		clip.start();
		clip.setFramePosition(0);
		clip.flush();
	}

	private void history(String link) {
		popupMenu.remove(uploads[uploads.length - 1]);

		for (int i = uploads.length - 1; i > 0; i--) {
			uploads[i] = uploads[i - 1];
		}
		uploads[0] = new MenuItem(link);
		uploads[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(e.getActionCommand()));
				} catch (URISyntaxException | IOException ex) {
					System.err.println(ex.toString());
				}
			}
		});
		popupMenu.insert(uploads[0], 2);
	}
        
        public void sendClipboard(){
            Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (clipboardContents == null) {
                new NotificationDialog("Clipboard Vuoto :( ", "");
            }
            else
                try {
                    
                    if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        PrintWriter out = new PrintWriter("clipboard.txt");
                        out.println((String) clipboardContents.getTransferData(DataFlavor.stringFlavor));
                        uploader = new Uploader(new Zipper(new File("clipboard.txt")).toZip(), ip, port);
			uploader.send(pass, "file");
                        new NotificationDialog("File Caricato!", uploader.getLink());
			history(uploader.getLink());
			clpbrd.setContents(new StringSelection(uploader.getLink()), null);
                        new File("clipboard.txt").delete();
                        new File("clipboard.zip").delete();
                    }
                }
                catch (UnsupportedFlavorException | IOException ex) {
                    System.err.println(ex.toString());
                }
        }
}
