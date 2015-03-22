import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class NotificationDialog {

	public NotificationDialog(String header, final String message) {

		final JDialog dialogFrame = new JDialog();

		dialogFrame.setSize(200, 50);
		dialogFrame.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		JLabel headingLabel = new JLabel(header);
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

		// Messaggio
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = 2;

		JLabel messageLabel = new JLabel(message);
		dialogFrame.add(messageLabel, constraints);
		dialogFrame.setVisible(true);

		// Per il osizionamento in basso a destra
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		// altezza taskbar
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(
				dialogFrame.getGraphicsConfiguration());
		dialogFrame.setLocation(scrSize.width - dialogFrame.getWidth(),
				scrSize.height - toolHeight.bottom - dialogFrame.getHeight());

		xButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogFrame.dispose();
			}
		});

		// Click nel JDialog per mandare all'url
		dialogFrame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					Desktop.getDesktop().browse(new URI(message));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					dialogFrame.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

}
