import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.imageio.ImageIO;

public class ScreenshotUploader {
	private BufferedImage img;
	private ByteArrayOutputStream outputArray;
	private Socket soc;
	private String link;

	public ScreenshotUploader(Rectangle r, String ip) throws IOException,
			AWTException {
		this.soc = new Socket(ip, 4030);
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		for (GraphicsDevice gd : GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices()) {
			screenRect = screenRect.union(gd.getDefaultConfiguration()
					.getBounds());
		}

		this.img = new Robot().createScreenCapture(screenRect).getSubimage(r.x,
				r.y, r.width, r.height);

		this.outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", this.outputArray);
		this.outputArray.flush();
	}

	public ScreenshotUploader(BufferedImage bi, String ip) throws IOException {
		this.soc = new Socket(ip, 4030);
		this.img = bi;

		this.outputArray = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", this.outputArray);
		this.outputArray.flush();
	}

	public void send(String pass) throws IOException {
		byte[] bytes = outputArray.toByteArray();
		outputArray.close();

		DataOutputStream os = new DataOutputStream(soc.getOutputStream());
		BufferedReader stringIn = new BufferedReader(new InputStreamReader(
				soc.getInputStream()));

		// send auth
		System.out.println("Invio auth");
		os.writeBytes(pass + "\n");
		System.out.println("Inviato auth: "+ pass);
		this.link = stringIn.readLine();
		// this.link = os.println();
		System.out.println("link: " + link);
		if (this.link.equals("OK")) {
			// image transfer
			System.out.println("trasferendo..");
			DataOutputStream dos = new DataOutputStream(soc.getOutputStream());

			dos.writeInt(bytes.length);
			dos.write(bytes, 0, bytes.length);

			// return link
			this.link = stringIn.readLine();

			dos.close();
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
