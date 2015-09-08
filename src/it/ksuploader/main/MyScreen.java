package it.ksuploader.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MyScreen extends JPanel{

    public Rectangle selectionBounds;
    private static Color c = new Color(255, 255, 255, 128);
    private Point startPoint = null;

    public MyScreen(){
        this.selectionBounds = new Rectangle();
        JDialog panel = new JDialog();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint.x + startPoint.y < e.getX() + e.getY()) {
                    selectionBounds.setBounds(startPoint.x ++, startPoint.y ++, e.getX() - startPoint.x --,
                            e.getY() - startPoint.y --);
                } else {
                    selectionBounds.setBounds(e.getX() + 1, e.getX() + 1, startPoint.x - e.getX() - 1,
                            startPoint.y - e.getX() - 1);
                }
                panel.removeAll();
                panel.dispose();

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                selectionBounds.x = Math.min(startPoint.x, e.getX());
                selectionBounds.y = Math.min(startPoint.y, e.getY());
                selectionBounds.width = Math.max(startPoint.x - e.getX(), e.getX() - startPoint.x);
                selectionBounds.height = Math.max(startPoint.y - e.getY(), e.getY() - startPoint.y);
                repaint();
            }
        };
        this.setOpaque(false);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        KeyListener keyHandler = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Main.myLog("Escape pressed during selection");
                    panel.removeAll();
                    panel.dispose();
                }
            }
        };
        panel.addKeyListener(keyHandler);
        panel.setModal(true);
        panel.setUndecorated(true);
        panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(getClass().getResource("/res/cursor.png")).getImage(),
                new Point(16, 16),
                "img"));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        panel.setLayout(new BorderLayout());
        panel.add(this);
        panel.setLocation(Main.so.getScreenBounds().getLocation());
        panel.setSize(Main.so.getScreenBounds().getSize());
        panel.setAlwaysOnTop(true);
        panel.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(c);

        Area fill = new Area(new Rectangle(new Point(0, 0), this.getSize()));
        fill.subtract(new Area(selectionBounds));
        g2d.fill(fill);
        g2d.setColor(Color.RED);
        g2d.draw(selectionBounds);
    }
}
