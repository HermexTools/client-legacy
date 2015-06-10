package it.ksuploader.main;

import it.ksuploader.dialogs.SettingsDialog;
import it.ksuploader.utils.MyKeyListener;
import it.ksuploader.utils.Sound;
import it.ksuploader.utils.Zipper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

import static java.awt.SystemTray.getSystemTray;
import static java.awt.SystemTray.isSupported;

public class SystemTrayMenu {

	private Clipboard clpbrd;
	private PopupMenu popupMenu;
	private static JFileChooser selFile;
	private final Sound suono;
	private TrayIcon trayIcon;

	private SettingsDialog configPanel;
	private MenuItem[] uploads;
	private MenuItem catturaArea;
	private MenuItem catturaDesktop;
	private MenuItem caricaFile;
	private MenuItem clipboard;

	public SystemTrayMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
		this.configPanel = new SettingsDialog();
		this.suono = new Sound();
		this.uploads = new MenuItem[5];

		for (int i = 0; i < uploads.length; i++) {
			uploads[i] = new MenuItem();
		}

		if (isSupported()) {
			try {
				clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				SystemTray systemTray = getSystemTray();

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
				catturaArea.addActionListener(e -> sendPartialScreen());
				catturaDesktop.addActionListener(e -> sendCompleteScreen());
				caricaFile.addActionListener(e -> sendFile());
				clipboard.addActionListener(e -> sendClipboard());

				settings.addActionListener(e -> {
					configPanel.loadCurrentConfig();
					configPanel.setVisible(true);
				});

				esci.addActionListener(e -> {
					getSystemTray().remove(trayIcon);
					Main.startUpCheck(Main.config.isStartUpEnabled());
					System.exit(0);
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
		StringBuffer ret = new StringBuffer("(");
		for (int e : keyNumber) {
			ret.append(MyKeyListener.fromKeyToName.get(e) + "+");
		}
		ret.replace(0,ret.length(),ret.substring(0, ret.length() - 1));
		ret.append(")");
		return ret.toString();
	}

	private class UploadPartialScreen extends SwingWorker<Void, Void> {
		public Void doInBackground() {
			PartialScreen partialScreen = new PartialScreen();

			// Nel caso in cui lo screen fosse annullato o 5x5
			if (partialScreen.getSelection() == null || partialScreen.getSelection().width <= 5
					|| partialScreen.getSelection().height <= 5) {

				// Annullo
				Main.dialog.show("Caricamento annullato!", ":(");
			} else {

				// Se FTP
				if (Main.config.getFtpEnabled()) {
					FtpUploader ftpUploader = new FtpUploader(partialScreen.getSelection());
					boolean res = false;
					res = ftpUploader.send();
					if (res) {
						Main.dialog.show("Screenshot Caricato!", ftpUploader.getLink());
						history(ftpUploader.getLink());
						clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
						suono.start();
					}

					// Se socket
				} else {

					Rectangle screenRect = new Rectangle(0, 0, 0, 0);
					for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
						screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
					}

					BufferedImage img;
					File tempFile = new File(Main.so.getTempDir() + "/ksutemp.png");
					try {

						img = new Robot().createScreenCapture(screenRect).getSubimage(partialScreen.getSelection().x,
								partialScreen.getSelection().y, partialScreen.getSelection().width,
								partialScreen.getSelection().height);

						ImageIO.write(img, "png", tempFile);

						if (Main.config.isSaveEnabled()) {
							ImageIO.write(img, "png",
									new File(Main.config.getSaveDir() + "/" + System.currentTimeMillis() / 1000
											+ new Random().nextInt(999) + ".png"));
							Main.myLog("[Uploader] Screen saved");
						}

						img.flush();

						Uploader uploader = new Uploader(tempFile.getPath());

						boolean res;
						res = uploader.send("img");
						if (res) {
							Main.dialog.show("Upload Completed!", uploader.getLink());
							history(uploader.getLink());
							clpbrd.setContents(new StringSelection(uploader.getLink()), null);
							tempFile.delete();
							suono.run();
						}
					} catch (IOException | AWTException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}

	private class UploadCompleteScreen extends SwingWorker<Void, Void> {
		public Void doInBackground() {
			try {
				Rectangle screenRect = new Rectangle(0, 0, 0, 0);
				for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
					screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
				}

				boolean res = false;

				// Se FTP
				if (Main.config.getFtpEnabled()) {
					FtpUploader ftpUploader = new FtpUploader(new Robot().createScreenCapture(screenRect));
					res = ftpUploader.send();
					if (res) {
						Main.dialog.show("Upload Completed!", ftpUploader.getLink());
						history(ftpUploader.getLink());
						clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
						suono.start();
					}

					// Se socket
				} else {

					File tempFile = new File(Main.so.getTempDir() + "/ksutemp.png");

					try {

						// ByteArrayOutputStream outputArray = new
						// ByteArrayOutputStream();
						// ImageIO.write(new
						// Robot().createScreenCapture(screenRect), "png",
						// outputArray);

						ImageIO.write(new Robot().createScreenCapture(screenRect), "png", tempFile);
						if (Main.config.isSaveEnabled()) {
							ImageIO.write(new Robot().createScreenCapture(screenRect), "png",
									new File(Main.config.getSaveDir() + "/" + System.currentTimeMillis() / 1000 + ""
											+ new Random().nextInt(999) + ".png"));
							Main.myLog("[Uploader] Screen saved");
						}

						Uploader uploader = new Uploader(tempFile.getPath());
						res = uploader.send("img");
						if (res) {
							Main.dialog.show("Upload Completed!", uploader.getLink());
							history(uploader.getLink());
							clpbrd.setContents(new StringSelection(uploader.getLink()), null);
							tempFile.delete();
							suono.run();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (AWTException ex) {
				ex.printStackTrace();
				Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
			}
			return null;
		}
	}

	private class UploadFile extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {

			boolean res;
			if (Main.config.getFtpEnabled()) {
				// Se non finisce con .zip O ci sono più file
				FtpUploader ftpUploader = null;
				if (!selFile.getSelectedFiles()[0].getName().endsWith(".zip") || selFile.getSelectedFiles().length > 1) {
					ftpUploader = new FtpUploader(new Zipper(selFile.getSelectedFiles()).toZip("ftp"));

					// Altrimenti se finisce con .zip O è uno solo
				} else if (selFile.getSelectedFiles()[0].getName().endsWith(".zip")
						|| selFile.getSelectedFiles().length == 1) {
					ftpUploader = new FtpUploader(selFile.getSelectedFiles()[0].getPath());
				}

				res = ftpUploader.send();
				if (res) {
					Main.dialog.show("Upload Completed!", ftpUploader.getLink());
					history(ftpUploader.getLink());
					clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
					suono.start();
				}

			} else {

				Uploader uploader = null;
				if (!selFile.getSelectedFiles()[0].getName().endsWith(".zip") || selFile.getSelectedFiles().length > 1) {
					uploader = new Uploader(new Zipper(selFile.getSelectedFiles()).toZip("socket"));
				} else if (selFile.getSelectedFiles()[0].getName().endsWith(".zip")
						|| selFile.getSelectedFiles().length == 1) {
					uploader = new Uploader(selFile.getSelectedFiles()[0].getPath());
				}
				res = uploader.send("file");
				if (res) {
					Main.dialog.show("Upload Completed!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					new File(Main.so.getTempDir(), "KStemp.zip").delete();
					suono.run();
				}

			}

			return null;
		}
	}

	public void sendPartialScreen() {
		new UploadPartialScreen().execute();

	}

	public void sendCompleteScreen() {
		new UploadCompleteScreen().execute();
	}

	public void sendFile() {

		try {
			selFile = new JFileChooser();
			selFile.setMultiSelectionEnabled(true);
			selFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (selFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				new UploadFile().execute();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
	}

	private void history(String link) {
		popupMenu.remove(uploads[uploads.length - 1]);

		System.arraycopy(uploads, 0, uploads, 1, uploads.length - 1);
		uploads[0] = new MenuItem(link);
		uploads[0].addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URI(e.getActionCommand()));
			} catch (URISyntaxException | IOException ex) {
				ex.printStackTrace();
				Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
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
				FtpUploader ftpUploader;
				ftpUploader = new FtpUploader(f.getPath());
				res = ftpUploader.send();
				if (res) {
					Main.dialog.show("Upload Completed!", ftpUploader.getLink());
					history(ftpUploader.getLink());
					clpbrd.setContents(new StringSelection(ftpUploader.getLink()), null);
					suono.start();
				}

			} else {

				Uploader uploader;
				uploader = new Uploader(f.getPath());
				res = uploader.send("txt");
				if (res) {
					Main.dialog.show("Upload Completed!", uploader.getLink());
					history(uploader.getLink());
					clpbrd.setContents(new StringSelection(uploader.getLink()), null);
					f.delete();
					suono.start();
				}
			}

		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
			Main.dialog.show("Error!", "Error with clipboard!");
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
	}
}
