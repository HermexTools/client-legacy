package it.ksuploader.main;

import it.ksuploader.dialogs.NotificationDialog;
import it.ksuploader.dialogs.ProgressDialog;

import it.ksuploader.utils.Environment;



import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Random;

import javax.imageio.ImageIO;

public class Uploader {
	private BufferedImage img;
	private byte[] bytes;
	private SocketChannel socketChannel;
	private String link;
	private String filePath;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ProgressDialog progressDialog;
    private static NotificationDialog dialog = new NotificationDialog();

	// Per gli screen parziali
	public Uploader(Rectangle r) {
		try {
			SocketChannel socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
			this.socketChannel = socketChannel;

			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
			}

			this.img = new Robot().createScreenCapture(screenRect).getSubimage(r.x, r.y, r.width, r.height);

			ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
			ImageIO.write(img, "png", outputArray);
			outputArray.flush();
			this.bytes = outputArray.toByteArray();
			outputArray.close();
            if(Main.config.isSaveEnabled()){
                ImageIO.write(img, "png", new File(Main.config.getSaveDir()+"/"+System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999)+".png"));
                Main.myLog("[Uploader] Screen saved");
            }
		} catch (AWTException | IOException ex) {
			ex.printStackTrace();
		}
	}

	// Per gli screen completi
	public Uploader(BufferedImage bi) {

		try {
			SocketChannel socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
			this.socketChannel = socketChannel;
			this.img = bi;

			ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
			ImageIO.write(img, "png", outputArray);
			outputArray.flush();
			this.bytes = outputArray.toByteArray();
			outputArray.close();
            if(Main.config.isSaveEnabled()){
                ImageIO.write(img, "png", new File(Main.config.getSaveDir()+"/"+System.currentTimeMillis() / 1000 + "" + new Random().nextInt(999)+".png"));
                Main.myLog("[Uploader] Screen saved");
            }
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Per i file
	public Uploader(String filePath) {

		SocketChannel socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
		this.socketChannel = socketChannel;
		this.filePath = filePath;
	}

	public boolean send(String type) {
		try {
			dos = new DataOutputStream(socketChannel.socket().getOutputStream());
			dis = new DataInputStream(socketChannel.socket().getInputStream());

			// socketChannel.socket().setSoTimeout(10000);

			// send auth
			Main.myLog("[Uploader] Sending auth");
			dos.writeUTF(Main.config.getPass());
			Main.myLog("[Uploader] Auth sent: " + Main.config.getPass());
			this.link = dis.readUTF();
			Main.myLog("[Uploader] Auth reply: " + link);
            
			if (this.link.equals("OK")) {

				Main.myLog("Sending type: " + type);
				dos.writeUTF(type);
                String srvResponse;
				// Controllo e aspetto che il server abbia ricevuto il type
				// corretto
                if (dis.readUTF().equals(type)) {
                    
                    Main.myLog("Il server riceve un: " + type);
                    

                        
                        switch (type) {
                            
                            // image transfer
                            case "img":
                                
                                Main.myLog("[Uploader] Uploading image...");
                                
                                dos.writeInt(bytes.length);
                                
                                srvResponse = dis.readUTF();
                                if(srvResponse.equals("START_TRANSFER")){
                                    dos.write(bytes, 0, bytes.length);
                                    dos.flush();
                                } else if(srvResponse.equals("SERVER_FULL")){
                                    Main.myLog("[Uploader] Server Full");
                                    dialog.serverFull();
                                }
                                
                                break;
                                
                                // file or txt transfer
                            case "file":
                            case "txt":
                                
                                File file = new File(filePath);
                                long fileLength = file.length();
                                Main.myLog("[Uploader] File length: " + fileLength);
                                dos.writeLong(fileLength);
                                srvResponse = dis.readUTF();
                                if(srvResponse.equals("START_TRANSFER")){
                                    sendFile(file,fileLength);
                                }else if(srvResponse.equals("FILE_TOO_LARGE")){
                                    Main.myLog("[Uploader] File too large");
                                    dialog.fileTooLarge();
                                    
                                } else if(srvResponse.equals("SERVER_FULL")){
                                    Main.myLog("[Uploader] Server Full");
                                    dialog.serverFull();
                                }
                                
                                progressDialog.setWait();
                                break;
                                
                                // default case, hmm
                            default:
                                // TODO
                                break;
                        }
                        
                        // return link
                        Main.myLog("[Uploader] Waiting link...");
                        this.link = dis.readUTF();
                        Main.myLog("[Uploader] Returned link: " + link);
                        if (type.equals("file") || type.equals("txt"))
                            progressDialog.close();
                    bytes = null;
				} else {
					Main.myLog("[Uploader] The server had a bad interpretation of the fileType");
					return false;
				}

			} else {
				Main.myLog("[Uploader] Wrong password, closed");
				dialog.wrongPassword();
				return false;
			}

			dos.close();
			dis.close();
			socketChannel.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void sendFile(File file, long fileLength) {
		RandomAccessFile aFile = null;
		try {
			
			aFile = new RandomAccessFile(file, "r");
			final FileChannel inChannel = aFile.getChannel();

			long bytesSent = 0; 

			progressDialog = new ProgressDialog();
			progressDialog.setUploader(this);
			progressDialog.setMessage("Caricando...");

			// send the file
			long bfSize = Math.min(131072, fileLength); // 128kB buffer
			while (bytesSent < fileLength) {
				bytesSent += inChannel.transferTo(bytesSent, bfSize, socketChannel);

				// To secure overflow
				try {
					Main.myLog("[Uploader] Sent: " + 100 * bytesSent / fileLength + "%");
					progressDialog.set((int) (100 * bytesSent / fileLength));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			progressDialog.setWait();
			inChannel.close();

			Thread.sleep(1000);
			Main.myLog("[Uploader] End of file reached..");
			aFile.close();
			Main.myLog("[Uploader] File closed.");

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public String getLink() {
		return link;
	}

	public void stopUpload() {
		try {
			dos.close();
			dis.close();
			socketChannel.close();
			new File(new Environment().getTempDir() + "/KStemp.zip").delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SocketChannel createChannel(String ip, int port) {

		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			socketChannel.connect(socketAddress);
			Main.myLog("[Uploader] Connected, now sending the file...");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return socketChannel;
	}

}
