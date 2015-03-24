import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import javax.imageio.ImageIO;

public class Uploader {
	private BufferedImage img;
	private byte[] bytes;
	private SocketChannel socketChannel;
	private String link;
	private String fileName;

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
	public Uploader(String fileName, String ip, int port) throws UnknownHostException, IOException {

		SocketChannel socketChannel = createChannel(ip, port);
		this.socketChannel = socketChannel;

		// this.socket = new Socket(ip, port);
		this.fileName = fileName;
	}

	public void send(String pass, String type) throws IOException {

		DataOutputStream dos = new DataOutputStream(socketChannel.socket().getOutputStream());
		BufferedReader stringIn = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));

		try {
			socketChannel.socket().setSoTimeout(10000);

			// send auth
			System.out.println("Sending auth");
			dos.writeBytes(pass + "\n");
			System.out.println("Auth sent: " + pass);
			this.link = stringIn.readLine();
			// this.link = os.println();
			System.out.println("Auth reply: " + link);
			if (this.link.equals("OK")) {

				System.out.println("Sending type: " + type);
				dos.writeBytes(type + "\n");

				// Controllo e aspetto che il server abbia ricevuto il type
				// corretto
				if (stringIn.readLine().equals(type)) {

					System.out.println("Il server riceve un: " + type);

					switch (type) {

					// image transfer
					case "img":

						System.out.println("Uploading image...");

						dos.writeInt(bytes.length);
						dos.write(bytes, 0, bytes.length);
						dos.flush();

						break;

					// file transfer
					case "file":

						// SocketChannel socketChannel = createChannel(ip,
						// filePort);
						sendFile(fileName);

						break;

					// default case, hmm
					default:

						break;
					}

					// return link
					System.out.println("Waiting link...");
					this.link = stringIn.readLine();
					System.out.println("Returned link: " + link);

					bytes = null;
				} else {
					System.out.println("The server had a bad interpretation of the fileType");
				}

			} else {
				System.out.println("Closed");
			}

			dos.close();
			stringIn.close();
			socketChannel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SocketChannel createChannel(String ip, int port) {

		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			socketChannel.connect(socketAddress);
			System.out.println("Connected, now sending the file...");

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
			FileChannel inChannel = aFile.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (inChannel.read(buffer) > 0) {
				buffer.flip();
				socketChannel.write(buffer);
				buffer.clear();
			}
			Thread.sleep(1000);
			System.out.println("End of file reached..");
			// socketChannel.close();
			aFile.close();
			System.out.println("File closed.");

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

}
