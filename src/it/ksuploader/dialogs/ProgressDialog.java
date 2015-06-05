package it.ksuploader.dialogs;

import it.ksuploader.main.FtpUploader;
import it.ksuploader.main.Main;
import it.ksuploader.main.Uploader;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog {
	private JProgressBar progressBar;
	private JLabel headingLabel;
	private Object callerUploader;

	public ProgressDialog() {

		// dialogFrame = new JDialog();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			Main.myLog(ex.toString());
		}

		setSize(200, 50);
		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		headingLabel = new JLabel();
		Font f = headingLabel.getFont();
		f = new Font(f.getFontName(), Font.BOLD, f.getSize());
		headingLabel.setFont(f);
		headingLabel.setOpaque(false);
		add(headingLabel, constraints);
		setUndecorated(true);

		// Bottone
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;

		JButton xButton = new JButton("X");
		xButton.setMargin(new Insets(1, 4, 1, 4));
		xButton.setFocusable(false);
		add(xButton, constraints);

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
		add(progressBar, constraints);

		xButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				stoppedUploaderClose();
			}
		});
	}

	private void autoPosition() {
		// Per il posizionamento in basso a destra
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		// altezza taskbar
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
		this.setLocation(scrSize.width - 5 - this.getWidth(), scrSize.height - 5 - toolHeight.bottom - this.getHeight());
	}

	public void destroy() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					for (float i = 1.00f; i >= 0; i -= 0.01f) {
						setOpacity(i);
						Thread.sleep(15);
					}
					dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
					Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
				}
			};
		}.start();

	}

	public void setUploader(Uploader callerUploader) {
		this.callerUploader = callerUploader;
	}

	public void set(int n) {
		progressBar.setValue(n);
		progressBar.setString(n + " %");
	}

	public void setMessage(String headingLabel) {
		this.headingLabel.setText(headingLabel);
		autoPosition();
		this.setShape(new RoundRectangle2D.Double(1, 1, 200, 50, 20, 20));
		this.setVisible(true);
	}

	public void setWait() {
		headingLabel.setText("Waiting link...");
	}

	public void close() {
		this.dispose();
	}

	public void stoppedUploaderClose() {
		try {
			if (callerUploader instanceof Uploader)
				((Uploader) callerUploader).stopUpload();
			else if (callerUploader instanceof FtpUploader)
				FtpUploader.getInstance(null).stopUpload();
		} catch (Exception e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
	}

}
