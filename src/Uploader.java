import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

public class Uploader {
	private BufferedImage img;
	private byte[] bytes;
	private Socket socket;
	private String link;
	long length;

	// Per gli screen parziali
	public Uploader(Rectangle r, String ip, int port) throws IOException, AWTException {
		this.socket = new Socket(ip, port);
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
		this.socket = new Socket(ip, port);
		this.img = bi;

		ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "png", outputArray);
		outputArray.flush();
		this.bytes = outputArray.toByteArray();
		outputArray.close();
	}

	// Per i file
	public Uploader(String fileName, String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);

		FileInputStream fis = new FileInputStream(fileName);
		ByteArrayOutputStream outputArray = new ByteArrayOutputStream();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}

		outputArray.flush();
		this.bytes = bos.toByteArray();
		outputArray.close();

	}

	public void send(String pass, String type) throws IOException {

		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		BufferedReader stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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

			// Controllo e aspetto che il server abbia ricevuto il type corretto
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

					System.out.println("Uploading file...");

					dos.writeInt(bytes.length);
					dos.write(bytes, 0, bytes.length);
					dos.flush();

					break;

				// default case, hmm
				default:

					break;
				}

				// return link
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
		socket.close();
	}

	public String getLink() {
		return link;
	}

}
