import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

public class Uploader {
	private BufferedImage img;
	private byte[] bytes;
	private Socket soc;
	private String link;
	InputStream is = null;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;

	// Per gli screen parziali
	public Uploader(Rectangle r, String ip) throws IOException, AWTException {
		this.soc = new Socket(ip, 4030);
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		for (GraphicsDevice gd : GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices()) {
			screenRect = screenRect.union(gd.getDefaultConfiguration()
					.getBounds());
		}

		this.img = new Robot().createScreenCapture(screenRect).getSubimage(r.x,
				r.y, r.width, r.height);

		ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "png", outputArray);
		outputArray.flush();
		this.bytes = outputArray.toByteArray();
		outputArray.close();
	}

	// Per gli screen completi
	public Uploader(BufferedImage bi, String ip) throws IOException {
		this.soc = new Socket(ip, 4030);
		this.img = bi;

		ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "png", outputArray);
		outputArray.flush();
		this.bytes = outputArray.toByteArray();
		outputArray.close();
	}

	// Per i file
	public Uploader(String string, String ip) throws UnknownHostException,
			IOException {
		this.soc = new Socket(ip, 4030);

		int bufferSize = soc.getReceiveBufferSize();
		this.bytes = new byte[bufferSize];

	}

	public void send(String pass, String type) throws IOException {

		DataOutputStream os = new DataOutputStream(soc.getOutputStream());
		BufferedReader stringIn = new BufferedReader(new InputStreamReader(
				soc.getInputStream()));

		// send auth
		System.out.println("Invio auth");
		os.writeBytes(pass + "\n");
		System.out.println("Inviato auth: " + pass);
		this.link = stringIn.readLine();
		// this.link = os.println();
		System.out.println("Auth reply: " + link);
		if (this.link.equals("OK")) {

			System.out.println("Sending type: " + type);
			os.writeBytes(type + "\n");

			// Controllo e aspetto che il server abbia ricevuto il type corretto
			if (stringIn.readLine().equals(type)) {

				switch (type) {

				case "img":

					// image transfer
					System.out.println("Trasferendo immagine...");
					DataOutputStream dos = new DataOutputStream(
							soc.getOutputStream());

					dos.writeInt(bytes.length);
					dos.write(bytes, 0, bytes.length);
					// dos.close();

					break;
				case "file":

					// Parte del receiver per server
					/*
					 * is = soc.getInputStream(); int count; while ((count =
					 * is.read(bytes)) > 0) { bos.write(bytes, 0, count); }
					 */

					/*
					 * long length = file.length(); if (length >
					 * Integer.MAX_VALUE) {
					 * System.out.println("File is too large."); } byte[] bytes
					 * = new byte[(int) length]; FileInputStream fis = new
					 * FileInputStream(file); BufferedInputStream bis = new
					 * BufferedInputStream(fis); BufferedOutputStream out = new
					 * BufferedOutputStream(socket.getOutputStream());
					 * 
					 * int count;
					 * 
					 * while ((count = bis.read(bytes)) > 0) { out.write(bytes,
					 * 0, count); }
					 * 
					 * out.flush(); out.close(); fis.close(); bis.close();
					 */
					// bos.flush();
					// bos.close();
					// is.close();

					break;
				default:

					break;
				}

				// return link
				this.link = stringIn.readLine();

				bytes = null;
			} else {
				System.out
						.println("Il server non ha interpretato la tipologia file");
			}

		} else {
			System.out.println("Chiuso");
		}

		os.close();
		stringIn.close();
		soc.close();
	}

	public String getLink() {
		return link;
	}

}
