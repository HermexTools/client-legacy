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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
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
	private MenuItem[] uploads;
	private final Sound suono;

	private String ip;
	private String pass;
	private int port;

	public SystemTrayMenu(String ip,String pswr, int port){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		

		this.ip = ip;
		this.pass = pswr;
		this.port = port;
                this.suono = new Sound();
		this.uploads = new MenuItem[5];

		for (int i = 0; i < uploads.length; i++) {
			uploads[i] = new MenuItem();
		}

		if (SystemTray.isSupported()) {
                        try {
                                clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                                systemTray = SystemTray.getSystemTray();
                                
                                trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/res/icon.png")), "ownPuush");
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
                                
                                // Gestione voci menu
                                catturaArea.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                sendPartialScreen();
                                        }
                                });
                                
                                catturaDesktop.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                sendCompleteScreen();
                                        }
                                });
                                
                                caricaFile.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                                sendFile();
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
                        } catch (IOException | AWTException ex) {
                                ex.printStackTrace();
                        }
		}
	}

	public void sendPartialScreen(){
		partialScreen = new PartialScreen();
                if (partialScreen.getSelection().width <= 5 || partialScreen.getSelection().height <= 5) {
                        
                        // Annullo
                        trayIcon.displayMessage("Info", "Caricamento annullato :(", TrayIcon.MessageType.INFO);
                } else {
                        
                        // Altrimenti invio
                        uploader = new Uploader(partialScreen.getSelection(), ip, port);
                        boolean res;
                        res = uploader.send(pass, "img");
                        if (res) {
                                new NotificationDialog().show("Screenshot Caricato!", uploader.getLink());
                                history(uploader.getLink());
                                clpbrd.setContents(new StringSelection(uploader.getLink()), null);
                                suono.run();
                        }
                }
	}

	public void sendCompleteScreen() {
                completeScreen = new CompleteScreen();
                uploader = new Uploader(completeScreen.getImg(), ip, port);
                boolean res = false;
                res = uploader.send(pass, "img");
                if (res) {
                        new NotificationDialog().show("Screenshot Caricato!", uploader.getLink());
                        history(uploader.getLink());
                        clpbrd.setContents(new StringSelection(uploader.getLink()), null);
                        suono.run();
                }
	}

	public void sendFile(){

		try {
			JFileChooser selFile = new JFileChooser();
			if (selFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				uploader = new Uploader(new Zipper(selFile.getSelectedFile()).toZip(), ip, port);
				boolean res = false;
				res = uploader.send(pass, "file");
				if (res) {
					new NotificationDialog().show("File Caricato!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					new File(selFile.getSelectedFile().getName() + ".zip").delete();
					suono.run();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
					ex.printStackTrace();
				}
			}
		});
		popupMenu.insert(uploads[0], 2);
	}

	public void sendClipboard() {
		try {
			String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
					.getData(DataFlavor.stringFlavor);
			File f = new File("clipboard.txt");
			PrintWriter out = new PrintWriter("clipboard.txt");
			out.println(clipboard);
			out.close();
			uploader = new Uploader(f.getPath(), ip, port);
			uploader.send(pass, "txt");
			new NotificationDialog().show("Clipboard Caricato!", uploader.getLink());
			history(uploader.getLink());
			clpbrd.setContents(new StringSelection(uploader.getLink()), null);
			f.delete();
			suono.run();
		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
			new NotificationDialog().show("Errore!", "Impossibile completare l'operazione");
		}
	}
}
