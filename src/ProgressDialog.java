import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ProgressDialog {
	private final JDialog dialogFrame;
	private JProgressBar progressBar;
	private JLabel headingLabel;

	public ProgressDialog() {

		dialogFrame = new JDialog();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}

		dialogFrame.setSize(200, 50);
		dialogFrame.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		headingLabel = new JLabel("Caricamento...");
		Font f = headingLabel.getFont();
		f = new Font(f.getFontName(), Font.BOLD, f.getSize());
		headingLabel.setFont(f);
		headingLabel.setOpaque(false);
		dialogFrame.add(headingLabel, constraints);
		dialogFrame.setUndecorated(true);

		// Bottone
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;

		JButton xButton = new JButton("X");
		xButton.setMargin(new Insets(1, 4, 1, 4));
		xButton.setFocusable(false);
		dialogFrame.add(xButton, constraints);

		// Progress bar
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = 2;

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		Dimension dim = new Dimension();
		dim.width = 130;
		dim.height = 20;
		progressBar.setMinimumSize(dim);
		progressBar.setStringPainted(true);
		progressBar.setBorderPainted(true);
		dialogFrame.add(progressBar, constraints);

		dialogFrame.setShape(new RoundRectangle2D.Double(1, 1, 200, 50, 20, 20));
		dialogFrame.setVisible(true);

		// Per il posizionamento in basso a destra
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		// altezza taskbar
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(dialogFrame.getGraphicsConfiguration());
		dialogFrame.setLocation(scrSize.width - 5 - dialogFrame.getWidth(), scrSize.height - 5 - toolHeight.bottom
				- dialogFrame.getHeight());

		xButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogFrame.dispose();
			}
		});

	}

	public void destroy() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					for (float i = 1.00f; i >= 0; i -= 0.01f) {
						dialogFrame.setOpacity(i);
						Thread.sleep(15);
					}
					dialogFrame.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	public void set(int n) {
		progressBar.setValue(n);
		progressBar.setString(n + "");
	}

	public void setWait() {

	}

	public void close() {
		dialogFrame.dispose();
	}

}
