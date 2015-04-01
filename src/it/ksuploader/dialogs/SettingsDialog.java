package it.ksuploader.dialogs;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {
	private final JTextField ftpAddr;
	private final JTextField ftpPort;
	private final JTextField ftpDir;
	private final JTextField ftpWeburl;
	private final JTextField ftpUser;
	private final JTextField ftpPassw;
	private final JTextField srvAddr;
	private final JTextField srvPassw;
	private final JTextField srvPort;
	private final JCheckBox ftpEnabled;
	private final JCheckBox saveEnabled;

	public SettingsDialog() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Settings");
		setBounds(100, 100, 480, 300);
		getContentPane().setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 228, 464, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		ftpEnabled = new JCheckBox("FTP Enabled?");
		ftpEnabled.setBounds(6, 7, 191, 23);
		getContentPane().add(ftpEnabled);
		{
			JPanel panel = new JPanel();
			panel.setBounds(0, 37, 464, 187);
			getContentPane().add(panel);
			panel.setLayout(null);
			{
				JLabel labelFtpUser = new JLabel("FTP User");
				labelFtpUser.setBounds(10, 111, 101, 14);
				panel.add(labelFtpUser);
			}
			{
				JLabel labelFtpAddr = new JLabel("FTP Address");
				labelFtpAddr.setBounds(10, 11, 101, 14);
				panel.add(labelFtpAddr);
			}
			{
				JLabel labelFtpDir = new JLabel("FTP Directory");
				labelFtpDir.setBounds(10, 61, 101, 14);
				panel.add(labelFtpDir);
			}
			{
				JLabel labelFtpWeburl = new JLabel("FTP Weburl");
				labelFtpWeburl.setBounds(10, 86, 101, 14);
				panel.add(labelFtpWeburl);
			}
			{
				JLabel labelFtpPort = new JLabel("FTP Port");
				labelFtpPort.setBounds(10, 36, 101, 14);
				panel.add(labelFtpPort);
			}
			{
				JLabel labelFtpPassw = new JLabel("FTP Password");
				labelFtpPassw.setBounds(10, 136, 101, 14);
				panel.add(labelFtpPassw);
			}

			ftpAddr = new JTextField();
			ftpAddr.setBounds(121, 8, 106, 20);
			panel.add(ftpAddr);
			ftpAddr.setColumns(10);

			ftpPort = new JTextField();
			ftpPort.setBounds(121, 33, 106, 20);
			panel.add(ftpPort);
			ftpPort.setColumns(10);

			ftpDir = new JTextField();
			ftpDir.setBounds(121, 58, 106, 20);
			panel.add(ftpDir);
			ftpDir.setColumns(10);

			ftpWeburl = new JTextField();
			ftpWeburl.setBounds(121, 83, 106, 20);
			panel.add(ftpWeburl);
			ftpWeburl.setColumns(10);

			ftpUser = new JTextField();
			ftpUser.setBounds(121, 108, 106, 20);
			panel.add(ftpUser);
			ftpUser.setColumns(10);

			ftpPassw = new JTextField();
			ftpPassw.setBounds(121, 133, 106, 20);
			panel.add(ftpPassw);
			ftpPassw.setColumns(10);

			JLabel labelSrvSrv = new JLabel("Server Address");
			labelSrvSrv.setBounds(237, 11, 107, 14);
			panel.add(labelSrvSrv);

			JLabel labelSrvPassw = new JLabel("Server Password");
			labelSrvPassw.setBounds(237, 36, 107, 14);
			panel.add(labelSrvPassw);

			JLabel labelSrvPort = new JLabel("Server Port");
			labelSrvPort.setBounds(237, 61, 107, 14);
			panel.add(labelSrvPort);

			srvAddr = new JTextField();
			srvAddr.setBounds(348, 8, 106, 20);
			panel.add(srvAddr);
			srvAddr.setColumns(10);

			srvPassw = new JTextField();
			srvPassw.setBounds(348, 33, 106, 20);
			panel.add(srvPassw);
			srvPassw.setColumns(10);

			srvPort = new JTextField();
			srvPort.setBounds(348, 58, 106, 20);
			panel.add(srvPort);
			srvPort.setColumns(10);
		}

		saveEnabled = new JCheckBox("Save a local copy of images");
		saveEnabled.setBounds(232, 7, 215, 23);
		getContentPane().add(saveEnabled);
	}

}
