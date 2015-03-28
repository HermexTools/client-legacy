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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

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

	// Per gli screen parziali
	public Uploader(Rectangle r, String ip, int port) throws IOException, AWTException {

		SocketChannel socketChannel = createChannel(ip, port);
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
	}

	// Per gli screen completi
	public Uploader(BufferedImage bi, String ip, int port) throws IOException {

		SocketChannel socketChannel = createChannel(ip, port);
		this.socketChannel = socketChannel;
		this.img = bi;

		ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "png", outputArray);
		outputArray.flush();
		this.bytes = outputArray.toByteArray();
		outputArray.close();
	}

	// Per i file
	public Uploader(String filePath, String ip, int port) throws UnknownHostException, IOException {

		SocketChannel socketChannel = createChannel(ip, port);
		this.socketChannel = socketChannel;
		this.filePath = filePath;
	}

	public boolean send(String pass, String type) throws IOException {

		dos = new DataOutputStream(socketChannel.socket().getOutputStream());
		dis = new DataInputStream(socketChannel.socket().getInputStream());

		try {
			// socketChannel.socket().setSoTimeout(10000);

			// send auth
			System.out.println("[Uploader] Sending auth");
			dos.writeUTF(pass);
			System.out.println("[Uploader] Auth sent: " + pass);
			this.link = dis.readUTF();
			System.out.println("[Uploader] Auth reply: " + link);
			if (this.link.equals("OK")) {

				System.out.println("Sending type: " + type);
				dos.writeUTF(type);

				// Controllo e aspetto che il server abbia ricevuto il type
				// corretto
				if (dis.readUTF().equals(type)) {

					System.out.println("Il server riceve un: " + type);

					switch (type) {

					// image transfer
					case "img":

						System.out.println("[Uploader] Uploading image...");

						dos.writeInt(bytes.length);
						dos.write(bytes, 0, bytes.length);
						dos.flush();

						break;

					// file or txt transfer
					case "file":
					case "txt":

						sendFile(filePath);
						progressDialog.setWait();
						break;

					// default case, hmm
					default:

						break;
					}

					// return link
					System.out.println("[Uploader] Waiting link...");
					this.link = dis.readUTF();
					System.out.println("[Uploader] Returned link: " + link);
					if (type.equals("file"))
						progressDialog.close();
					bytes = null;
				} else {
					System.out.println("[Uploader] The server had a bad interpretation of the fileType");
					return false;
				}

			} else {
				System.out.println("[Uploader] Wrong password, closed");
				new NotificationDialog().wrongPassword();
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

	public SocketChannel createChannel(String ip, int port) {

		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			socketChannel.connect(socketAddress);
			System.out.println("[Uploader] Connected, now sending the file...");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return socketChannel;
	}

	public void sendFile(String fileName) {
		RandomAccessFile aFile = null;
		try {
			File file = new File(fileName);
			aFile = new RandomAccessFile(file, "r");
			final FileChannel inChannel = aFile.getChannel();

			long bytesSent = 0, fileLength = file.length();
			System.out.println("[Uploader] File length: " + fileLength);
			dos.writeLong(fileLength);
			progressDialog = new ProgressDialog();
			progressDialog.setUploader(this);
			progressDialog.setMessage("Caricando...");

			// send the file
			while (bytesSent < fileLength) {
				bytesSent += inChannel.transferTo(bytesSent, fileLength - bytesSent, socketChannel);

				// To secure overflow
				try {
					System.out.println("[Uploader] Sent: " + 100 * bytesSent / fileLength + "%");
					progressDialog.set((int) (100 * bytesSent / fileLength));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			progressDialog.setWait();
			inChannel.close();

			Thread.sleep(1000);
			System.out.println("[Uploader] End of file reached..");
			aFile.close();
			System.out.println("[Uploader] File closed.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
