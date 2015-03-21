import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PartialScreen extends JPanel {

	private Rectangle selectionBounds;
	private Point fromClickPoint;
	private Point toClickPoint;
	private JDialog frame;

	public PartialScreen() {
		setOpaque(false);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
		}

		frame = new JDialog();
		frame.setModal(true);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this);
		Rectangle bounds = getVirtualBounds();
		frame.setLocation(bounds.getLocation());
		frame.setSize(bounds.getSize());
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

	MouseAdapter mouseHandler = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			fromClickPoint = e.getPoint();
			selectionBounds = null;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			toClickPoint = e.getPoint();
			frame.dispose();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point dragPoint = e.getPoint();
			int x = Math.min(fromClickPoint.x, dragPoint.x);
			int y = Math.min(fromClickPoint.y, dragPoint.y);
			int width = Math.max(fromClickPoint.x - dragPoint.x, dragPoint.x
					- fromClickPoint.x);
			int height = Math.max(fromClickPoint.y - dragPoint.y, dragPoint.y
					- fromClickPoint.y);
			selectionBounds = new Rectangle(x, y, width, height);
			repaint();
		}
	};

	public Rectangle getVirtualBounds() {
		Rectangle bounds = new Rectangle(0, 0, 0, 0);

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice lstGDs[] = ge.getScreenDevices();
		for (GraphicsDevice gd : lstGDs) {
			bounds.add(gd.getDefaultConfiguration().getBounds());
		}
		return bounds;
	}

	public Rectangle getSelection() {
		if (fromClickPoint.x + fromClickPoint.y < toClickPoint.x
				+ toClickPoint.y)
			return new Rectangle(fromClickPoint.x + 1, fromClickPoint.y + 1,
					toClickPoint.x - fromClickPoint.x - 1, toClickPoint.y
							- fromClickPoint.y - 1);
		else
			return new Rectangle(toClickPoint.x + 1, toClickPoint.y + 1,
					fromClickPoint.x - toClickPoint.x - 1, fromClickPoint.y
							- toClickPoint.y - 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(new Color(255, 255, 255, 128));

		Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
		if (selectionBounds != null) {
			fill.subtract(new Area(selectionBounds));
		}
		g2d.fill(fill);
		if (selectionBounds != null) {
			g2d.setColor(Color.RED);
			g2d.draw(selectionBounds);
		}
		g2d.dispose();
	}
}
