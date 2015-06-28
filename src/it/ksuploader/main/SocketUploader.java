package it.ksuploader.main;



import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class SocketUploader implements Observer{
	private SocketChannel socketChannel;
	private String link;
	private String filePath;
	private DataOutputStream dos;
	private DataInputStream dis;

	// Per i file
	public SocketUploader() {
		this.socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
		try {
			this.dos = new DataOutputStream(socketChannel.socket().getOutputStream());
			this.dis = new DataInputStream(socketChannel.socket().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean send(String type) {
		try {
			this.socketChannel = createChannel(Main.config.getIp(), Main.config.getPort());
			this.dos = new DataOutputStream(socketChannel.socket().getOutputStream());
			this.dis = new DataInputStream(socketChannel.socket().getInputStream());

			// send auth
			Main.myLog("[SocketUploader] Sending auth");
			dos.writeUTF(Main.config.getPass());
			Main.myLog("[SocketUploader] Auth sent: " + Main.config.getPass());
			this.link = dis.readUTF();
			Main.myLog("[SocketUploader] Auth reply: " + link);

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
					Main.myLog("[SocketUploader] File length: " + fileLength);
					dos.writeLong(fileLength);
					srvResponse = dis.readUTF();
					switch (srvResponse) {
					case "START_TRANSFER":
						sendFile(file, fileLength);

						break;
					case "FILE_TOO_LARGE":
						Main.myLog("[SocketUploader] File too large");
						Main.dialog.fileTooLarge();

						break;
					case "SERVER_FULL":
						Main.myLog("[SocketUploader] Server Full");
						Main.dialog.serverFull();
						break;
					}



					// return link
					Main.myLog("[SocketUploader] Waiting link...");
					this.link = dis.readUTF();
					Main.myLog("[SocketUploader] Returned link: " + link);
					Main.dialog.destroy();
				} else {
					Main.myLog("[SocketUploader] The server had a bad interpretation of the fileType");
					return false;
				}

			} else {
				Main.myLog("[SocketUploader] Wrong password, closed");
				Main.dialog.wrongPassword();
				return false;
			}

			dos.flush();

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

			Main.dialog.show("Uploading...", "", false);

			// send the file
			long bfSize = Math.min(16384, fileLength); // 128kB buffer
			while (bytesSent < fileLength) {
				bytesSent += inChannel.transferTo(bytesSent, bfSize, socketChannel);

				Main.myLog("[SocketUploader] Sent: " + 100 * bytesSent / fileLength + "%");
				Main.dialog.set((int) (100 * bytesSent / fileLength));
			}

			inChannel.close();

			Main.myLog("[SocketUploader] End of file reached..");
			aFile.close();
			Main.myLog("[SocketUploader] File closed.");

			Main.dialog.setWait();

		} catch (IOException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}

	}

	public void stopUpload() {
		try {
			dos.close();
			dis.close();
			socketChannel.close();
			new File(Main.so.getTempDir(), "KStemp.zip").delete();
            Main.dialog.show("Stopped...", "", false);
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
			Main.myLog("[SocketUploader] Reloaded socket");

		} catch (IOException | UnresolvedAddressException e) {
			e.printStackTrace();
			Main.dialog.connectionError();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
		return socketChannel;
	}

    public void closeSocket(){
        try {
            dos.close();
            dis.close();
            socketChannel.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getLink() {
		return link;
	}

	@Override
	public void update(Observable o, Object arg) {
        stopUpload();
	}
}
