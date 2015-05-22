package it.ksuploader.main;

import it.ksuploader.utils.Environment;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;

public class Uploader {
	private SocketChannel socketChannel;
	private String link;
	private String filePath;
	private DataOutputStream dos;
	private DataInputStream dis;

	// Per i file
		public Uploader(String filePath) {

		this.socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
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

					File file = new File(filePath);
					long fileLength = file.length();
					Main.myLog("[Uploader] File length: " + fileLength);
					dos.writeLong(fileLength);
					srvResponse = dis.readUTF();
					switch (srvResponse) {
						case "START_TRANSFER":
							sendFile(file, fileLength);

							break;
						case "FILE_TOO_LARGE":
							Main.myLog("[Uploader] File too large");
							Main.dialog.fileTooLarge();

							break;
						case "SERVER_FULL":
							Main.myLog("[Uploader] Server Full");
							Main.dialog.serverFull();
							break;
					}

					Main.progressDialog.setWait();

					// return link
					Main.myLog("[Uploader] Waiting link...");
					this.link = dis.readUTF();
					Main.myLog("[Uploader] Returned link: " + link);
					Main.progressDialog.close();
				} else {
					Main.myLog("[Uploader] The server had a bad interpretation of the fileType");
					return false;
				}

			} else {
				Main.myLog("[Uploader] Wrong password, closed");
				Main.dialog.wrongPassword();
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
		RandomAccessFile aFile;
		try {

			aFile = new RandomAccessFile(file, "r");
			final FileChannel inChannel = aFile.getChannel();

			long bytesSent = 0;

			// Main.progressDialog = new ProgressDialog();
			Main.progressDialog.setUploader(this);
			Main.progressDialog.setMessage("Uploading...");

			// send the file
			long bfSize = Math.min(16384, fileLength); // 128kB buffer
			while (bytesSent < fileLength) {
				bytesSent += inChannel.transferTo(bytesSent, bfSize, socketChannel);

				Main.myLog("[Uploader] Sent: " + 100 * bytesSent / fileLength + "%");
				Main.progressDialog.set((int) (100 * bytesSent / fileLength));
			}
			inChannel.close();

			Main.myLog("[Uploader] End of file reached..");
			aFile.close();
			Main.myLog("[Uploader] File closed.");

		} catch (IOException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
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
			new File(new Environment().getTempDir(), "KStemp.zip").delete();
		} catch (IOException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
	}

	private SocketChannel createChannel(String ip, int port) {

		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			socketChannel.connect(socketAddress);
			Main.myLog("[Uploader] Connected, now sending the file...");

		} catch (IOException | UnresolvedAddressException e) {
			e.printStackTrace();
			Main.dialog.connectionError();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
		return socketChannel;
	}

}
