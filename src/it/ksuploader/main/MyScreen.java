package it.ksuploader.main;

import org.jnativehook.GlobalScreen;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.IOException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

@SuppressWarnings("serial")
public class MyScreen extends JPanel implements NativeKeyListener {

	private Rectangle selectionBounds;
	private Point fromClickPoint = null;
	private Point toClickPoint;
	private JDialog frame;
    private static Color c = new Color(255, 255, 255, 128);

	public MyScreen() {

		// Set the event dispatcher to a swing safe executor service.
		GlobalScreen.setEventDispatcher(new SwingDispatchService());

		GlobalScreen.addNativeKeyListener(this);

        selectionBounds = new Rectangle();

		MouseAdapter mouseHandler = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				fromClickPoint = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				toClickPoint = e.getPoint();
				frame.dispose();

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point dragPoint = e.getPoint();
				selectionBounds.setBounds(
                        Math.min(fromClickPoint.x, dragPoint.x),
                        Math.min(fromClickPoint.y, dragPoint.y),
                        Math.max(fromClickPoint.x - dragPoint.x, dragPoint.x - fromClickPoint.x),
                        Math.max(fromClickPoint.y - dragPoint.y, dragPoint.y - fromClickPoint.y)
                );
				repaint();
			}
		};

		setOpaque(false);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		Toolkit tk = Toolkit.getDefaultToolkit();

		frame = new JDialog();
		frame.setModal(true);
		frame.setUndecorated(true);
		try {
			frame.setCursor(tk.createCustomCursor(ImageIO.read(getClass().getResource("/res/cursor.png")),
                    new Point(16, 16),
					"img"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this);
		frame.setLocation(getScreenBounds().getLocation());
		frame.setSize(getScreenBounds().getSize());
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

	public static Rectangle getScreenBounds() {
        Rectangle myScreen = new Rectangle(0, 0, 0, 0);
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            myScreen = myScreen.union(gd.getDefaultConfiguration().getBounds());
        }
        return myScreen;
	}

	public Rectangle getScreenSelection() {
		GlobalScreen.removeNativeKeyListener(this);
		if (fromClickPoint != null)
			if (fromClickPoint.x + fromClickPoint.y < toClickPoint.x + toClickPoint.y) {
				return new Rectangle(fromClickPoint.x + 1, fromClickPoint.y + 1, toClickPoint.x - fromClickPoint.x - 1,
						toClickPoint.y - fromClickPoint.y - 1);
			}
			else {
				return new Rectangle(toClickPoint.x + 1, toClickPoint.y + 1, fromClickPoint.x - toClickPoint.x - 1,
						fromClickPoint.y - toClickPoint.y - 1);
			}
		else {
			return null;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.setColor(c);

		Area fill = new Area(new Rectangle(new Point(0, 0), this.getSize()));
		fill.subtract(new Area(selectionBounds));

		((Graphics2D) g).fill(fill);
		g.setColor(Color.RED);
		((Graphics2D) g).draw(fill);//draw(selectionBounds);

        fill = null;
		g.dispose();
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativekeyevent) {
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
			Main.myLog("Escape pressed during selection");
			GlobalScreen.removeNativeKeyListener(this);
			frame.dispose();
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}
}
