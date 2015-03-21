package sergix44;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class CompleteScreen {
    private BufferedImage img;
    
    public CompleteScreen() {
		try {
			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			for (GraphicsDevice gd : GraphicsEnvironment
					.getLocalGraphicsEnvironment().getScreenDevices()) {
				screenRect = screenRect.union(gd.getDefaultConfiguration()
						.getBounds());
			}
			this.img = new Robot().createScreenCapture(screenRect);
		} catch (AWTException ex) {
			System.err.println(ex.toString());
		}
	}
    
    public BufferedImage getImg() {
        return img;
    }
    
    public void setImg(BufferedImage img) {
        this.img = img;
    }
    
    
}
