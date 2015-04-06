package it.ksuploader.main;

import it.ksuploader.dialogs.NotificationDialog;
import it.ksuploader.dialogs.SettingsDialog;
import it.ksuploader.utils.MyKeyListener;
import it.ksuploader.utils.Sound;
import it.ksuploader.utils.Zipper;

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
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SystemTrayMenu {

	private Clipboard clpbrd;
	private CompleteScreen completeScreen;
	private FtpUploader ftpUploader;
	private PartialScreen partialScreen;
	private PopupMenu popupMenu;
	private static JFileChooser selFile;
	private final Sound suono;
	private static SystemTray systemTray;
	private TrayIcon trayIcon;
	private Uploader uploader;

	private NotificationDialog notification;
	private SettingsDialog configPanel;
	private MenuItem[] uploads;
	private MenuItem catturaArea;
	private MenuItem catturaDesktop;
	private MenuItem caricaFile;
	private MenuItem clipboard;

	public SystemTrayMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
		this.notification = new NotificationDialog();
		this.configPanel = new SettingsDialog();
		this.suono = new Sound();
		this.uploads = new MenuItem[5];

		for (int i = 0; i < uploads.length; i++) {
			uploads[i] = new MenuItem();
		}

		if (SystemTray.isSupported()) {
			try {
				clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				systemTray = SystemTray.getSystemTray();

				trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/res/icon.png")), "KSUploader");
				trayIcon.setImageAutoSize(true);

				popupMenu = new PopupMenu();
				catturaArea = new MenuItem();
				catturaDesktop = new MenuItem();
				caricaFile = new MenuItem();
				clipboard = new MenuItem();
				updateKeys();
				MenuItem settings = new MenuItem("Settings");
				MenuItem esci = new MenuItem("Quit");

				popupMenu.add("Recent Uploads");
				popupMenu.addSeparator();
				popupMenu.addSeparator();
				popupMenu.add(catturaArea);
				popupMenu.add(catturaDesktop);
				popupMenu.addSeparator();
				popupMenu.add(caricaFile);
				popupMenu.add(clipboard);
				popupMenu.addSeparator();
				popupMenu.add(settings);
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

				settings.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						configPanel.loadCurrentConfig();
						configPanel.setVisible(true);
					}
				});

				esci.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SystemTray.getSystemTray().remove(trayIcon);
                        Main.startUpCheck(Main.config.isStartUpEnabled());
						Main.log.close();
						System.exit(0);
					}
				});

				trayIcon.setPopupMenu(popupMenu);
				systemTray.add(trayIcon);
			} catch (IOException | AWTException ex) {
				ex.printStackTrace();
				Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
			}
		}
	}

	public void updateKeys() {
		catturaArea.setLabel(("Capture Area " + loadKey(Main.config.getKeyScreen())));
		catturaDesktop.setLabel("Capture Desktop " + loadKey(Main.config.getKeyCScreen()));
		caricaFile.setLabel("Upload File " + loadKey(Main.config.getKeyFile()));
		clipboard.setLabel("Upload Clipboard " + loadKey(Main.config.getKeyClipboard()));
	}

	public String loadKey(int keyNumber[]) {
		String ret = "(";
		for (int e : keyNumber) {
			ret = ret + MyKeyListener.fromKeyToName.get(e) + "+";
		}
		ret = ret.substring(0, ret.length() - 1);
		ret += ")";
		return ret;
	}

	public void sendPartialScreen() {
		partialScreen = new PartialScreen();

		// Nel caso in cui lo screen fosse annullato o 5x5
		if (partialScreen.getSelection() == null || partialScreen.getSelection().width <= 5
				|| partialScreen.getSelection().height <= 5) {

			// Annullo
			trayIcon.displayMessage("Info", "Caricamento annullato :(", TrayIcon.MessageType.INFO);
		} else {

			if (Main.config.getFtpEnabled()) {
				ftpUploader = new FtpUploader(partialScreen.getSelection());
				boolean res = false;
				res = ftpUploader.send("img");
				if (res) {
					notification.show("Screenshot Caricato!", ftpUploader.getLink());
					history(ftpUploader.getLink());
					clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
					suono.run();
				}
			} else {
				// Altrimenti invio
				uploader = new Uploader(partialScreen.getSelection());
				boolean res;
				res = uploader.send("img");
				if (res) {
					notification.show("Screenshot Caricato!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					suono.run();
				}
			}

		}

	}

	public void sendCompleteScreen() {
		completeScreen = new CompleteScreen();
		boolean res = false;
		if (Main.config.getFtpEnabled()) {
			ftpUploader = new FtpUploader(completeScreen.getImg());
			res = ftpUploader.send("img");
			if (res) {
				notification.show("Screenshot Caricato!", ftpUploader.getLink());
				history(ftpUploader.getLink());
				clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
				suono.run();
			}
		} else {

			uploader = new Uploader(completeScreen.getImg());
			res = uploader.send("img");
			if (res) {
				notification.show("Screenshot Caricato!", uploader.getLink());
				history(uploader.getLink());
				clpbrd.setContents(new StringSelection(uploader.getLink()), null);
				suono.run();
			}

		}

	}

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {

			boolean res = false;
			if (Main.config.getFtpEnabled()) {
				// Se non finisce con .zip O ci sono più file
				if (!selFile.getSelectedFiles()[0].getName().endsWith(".zip") || selFile.getSelectedFiles().length > 1) {
					ftpUploader = new FtpUploader(new Zipper(selFile.getSelectedFiles()).toZip());

					// Altrimenti se finisce con .zip O è uno solo
				} else if (selFile.getSelectedFiles()[0].getName().endsWith(".zip")
						|| selFile.getSelectedFiles().length == 1) {
					ftpUploader = new FtpUploader(selFile.getSelectedFiles()[0].getPath());
				}

				res = ftpUploader.send("file");
				if (res) {
					notification.show("File Caricato!", ftpUploader.getLink());
					history(ftpUploader.getLink());
					clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
					new File(Main.so.getTempDir(),"KStemp.zip").delete();
					suono.run();
				}

			} else {

				if (!selFile.getSelectedFiles()[0].getName().endsWith(".zip") || selFile.getSelectedFiles().length > 1) {
					uploader = new Uploader(new Zipper(selFile.getSelectedFiles()).toZip());
				} else if (selFile.getSelectedFiles()[0].getName().endsWith(".zip")
						|| selFile.getSelectedFiles().length == 1) {
					uploader = new Uploader(selFile.getSelectedFiles()[0].getPath());
				}
				res = uploader.send("file");
				if (res) {
					notification.show("File Caricato!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					new File(Main.so.getTempDir(), "KStemp.zip").delete();
					suono.run();
				}

			}

			return null;
		}
	}

	public void sendFile() {

		try {
			selFile = new JFileChooser();
			selFile.setMultiSelectionEnabled(true);
			selFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (selFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				new Task().execute();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
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
					Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
				}
			}
		});
		popupMenu.insert(uploads[0], 2);
	}

	public void sendClipboard() {
		boolean res = false;
		try {

			String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
					.getData(DataFlavor.stringFlavor);
			String fileName = System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999);
			File f = new File(Main.so.getTempDir() + "/" + fileName + ".txt");
			Main.myLog(f.getPath());
			PrintWriter out = new PrintWriter(Main.so.getTempDir() + "/" + fileName + ".txt");
			out.println(clipboard);
			out.close();

			if (Main.config.getFtpEnabled()) {

				ftpUploader = new FtpUploader(f.getPath());
				res = ftpUploader.send("txt");
				if (res) {
					notification.show("Clipboard Caricata!", ftpUploader.getLink());
					history(ftpUploader.getLink());
					clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
					f.delete();
					suono.run();
				}

			} else {

				uploader = new Uploader(f.getPath());
				res = uploader.send("txt");
				if (res) {
					notification.show("Clipboard Caricata!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					f.delete();
					suono.run();
				}
			}

		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
			notification.show("Errore!", "Impossibile completare l'operazione");
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
	}
}
