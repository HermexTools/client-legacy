package it.ksuploader.dialogs;

import it.ksuploader.main.Main;
import it.ksuploader.utils.MyKeyListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static it.ksuploader.main.Main.myLog;

@SuppressWarnings("serial")
public class SettingsDialog extends JDialog implements NativeKeyListener {
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
    private final JCheckBox startUpEnabled;
    private final JCheckBox ftpesEnabled;
    private final JCheckBox acceptCertificates;
    private JFileChooser saveDir;
    private final JButton btnScreen;
    private final JButton btnCScreen;
    private final JButton btnFile;
    private final JButton btnClipboard;

    public SettingsDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setBounds(100, 100, 480, 330);
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);

        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        ftpEnabled = new JCheckBox("Use FTP");
        ftpEnabled.setBounds(6, 7, 130, 23);
        getContentPane().add(ftpEnabled);

        ftpEnabled.addActionListener(e -> update());

        saveEnabled = new JCheckBox("Save a local copy of images");
        saveEnabled.setBounds(237, 7, 200, 23);
        getContentPane().add(saveEnabled);

        saveEnabled.addActionListener(e -> {
            if (saveEnabled.isSelected()) {
                saveDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (saveDir.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                    saveDir.setSelectedFile(new File("."));
                Main.myLog("[Settings] local save enabled");
            } else {
                saveDir.setSelectedFile(new File("."));
                Main.myLog("[Settings] local save disabled");
            }
        });

        ftpesEnabled = new JCheckBox("Use FTPES");
        ftpesEnabled.setBounds(6, 33, 130, 23);
        getContentPane().add(ftpesEnabled);

        ftpesEnabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ftpesEnabled.isSelected())
                    acceptCertificates.setEnabled(true);
                else
                    acceptCertificates.setEnabled(false);
            }
        });

        startUpEnabled = new JCheckBox("Open at startup");
        startUpEnabled.setBounds(237, 33, 150, 23);
        getContentPane().add(startUpEnabled);

        //startUpEnabled.addActionListener(e -> Main.startUpCheck());

        JPanel panel = new JPanel();
        panel.setBounds(0, 63, 474, 238);
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel labelFtpUser = new JLabel("FTP User");
        labelFtpUser.setBounds(10, 111, 101, 14);
        panel.add(labelFtpUser);

        JLabel labelFtpAddr = new JLabel("FTP Address");
        labelFtpAddr.setBounds(10, 11, 101, 14);
        panel.add(labelFtpAddr);

        JLabel labelFtpDir = new JLabel("FTP Directory");
        labelFtpDir.setBounds(10, 61, 101, 14);
        panel.add(labelFtpDir);

        JLabel labelFtpWeburl = new JLabel("FTP Weburl");
        labelFtpWeburl.setBounds(10, 86, 101, 14);
        panel.add(labelFtpWeburl);

        JLabel labelFtpPort = new JLabel("FTP Port");
        labelFtpPort.setBounds(10, 36, 101, 14);
        panel.add(labelFtpPort);

        JLabel labelFtpPassw = new JLabel("FTP Password");
        labelFtpPassw.setBounds(10, 136, 101, 14);
        panel.add(labelFtpPassw);

        ftpAddr = new JTextField();
        ftpAddr.setBounds(100, 8, 127, 20);
        panel.add(ftpAddr);
        ftpAddr.setColumns(10);

        ftpPort = new JTextField();
        ftpPort.setBounds(100, 33, 127, 20);
        panel.add(ftpPort);
        ftpPort.setColumns(10);

        ftpDir = new JTextField();
        ftpDir.setBounds(100, 58, 127, 20);
        panel.add(ftpDir);
        ftpDir.setColumns(10);

        ftpWeburl = new JTextField();
        ftpWeburl.setBounds(100, 83, 127, 20);
        panel.add(ftpWeburl);
        ftpWeburl.setColumns(10);

        ftpUser = new JTextField();
        ftpUser.setBounds(100, 108, 127, 20);
        panel.add(ftpUser);
        ftpUser.setColumns(10);

        ftpPassw = new JTextField();
        ftpPassw.setBounds(100, 133, 127, 20);
        panel.add(ftpPassw);
        ftpPassw.setColumns(10);

        acceptCertificates = new JCheckBox("Accept Certificates");
        acceptCertificates.setBounds(98, 158, 127, 20);
        panel.add(acceptCertificates);

        acceptCertificates.addActionListener(e -> {
        });

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
        srvAddr.setBounds(327, 8, 127, 20);
        panel.add(srvAddr);
        srvAddr.setColumns(10);

        srvPassw = new JTextField();
        srvPassw.setBounds(327, 33, 127, 20);
        panel.add(srvPassw);
        srvPassw.setColumns(10);

        srvPort = new JTextField();
        srvPort.setBounds(327, 58, 127, 20);
        panel.add(srvPort);
        srvPort.setColumns(10);

        JButton okButton = new JButton("OK");
        okButton.setBounds(327, 204, 65, 23);
        panel.add(okButton);
        okButton.setActionCommand("OK");

        okButton.addActionListener(e -> {
            Main.config.storeNewConfig(ftpWeburl.getText(), ftpDir.getText(), ftpPort.getText(),
                    ftpPassw.getText(), ftpUser.getText(), ftpAddr.getText(), ftpEnabled.isSelected() + "",
                    srvPassw.getText(), srvPort.getText(), srvAddr.getText(), saveEnabled.isSelected() + "",
                    saveDir.getSelectedFile().getPath(), startUpEnabled.isSelected() + "",
                    ftpesEnabled.isSelected() + "", acceptCertificates.isSelected() + "");
            Main.startUpCheck();
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(402, 204, 65, 23);
        panel.add(cancelButton);
        cancelButton.setActionCommand("Cancel");

        cancelButton.addActionListener(e -> setVisible(false));

        btnScreen = new JButton("Change");
        btnScreen.setBounds(327, 83, 127, 20);
        btnScreen.setFont(btnScreen.getFont().deriveFont(9f));
        panel.add(btnScreen);

        btnScreen.addActionListener(e -> {
            enableListener(btnScreen);
            btnScreen.setEnabled(false);
        });

        btnCScreen = new JButton("Change");
        btnCScreen.setBounds(327, 108, 127, 20);
        btnCScreen.setFont(btnCScreen.getFont().deriveFont(9f));
        panel.add(btnCScreen);

        btnCScreen.addActionListener(e -> {
            enableListener(btnCScreen);
            btnCScreen.setEnabled(false);
        });

        btnFile = new JButton("Change");
        btnFile.setBounds(327, 133, 127, 20);
        btnFile.setFont(btnFile.getFont().deriveFont(9f));
        panel.add(btnFile);

        btnFile.addActionListener(e -> {
            enableListener(btnFile);
            btnFile.setEnabled(false);
        });

        btnClipboard = new JButton("Change");
        btnClipboard.setBounds(327, 158, 127, 20);
        btnClipboard.setFont(btnClipboard.getFont().deriveFont(9f));
        panel.add(btnClipboard);

        btnClipboard.addActionListener(e -> {
            enableListener(btnClipboard);
            btnClipboard.setEnabled(false);
        });

        JLabel lblFileShortcut = new JLabel("File shortcut");
        lblFileShortcut.setBounds(237, 136, 80, 14);
        panel.add(lblFileShortcut);

        JLabel lblClipboard = new JLabel("Clipboard shortcut");
        lblClipboard.setBounds(237, 161, 94, 14);
        panel.add(lblClipboard);

        JLabel lblAreaShortcut = new JLabel("Area shortcut");
        lblAreaShortcut.setBounds(237, 86, 90, 14);
        panel.add(lblAreaShortcut);

        JLabel lblNewLabel = new JLabel("Desktop shortcut");
        lblNewLabel.setBounds(237, 111, 87, 14);
        panel.add(lblNewLabel);
    }

	/*
     * LOADER + STATUS_UPDATER
	 */

    public void loadCurrentConfig() {
        try {
            this.ftpAddr.setText(Main.config.getFtpAddr());
            this.ftpDir.setText(Main.config.getFtpDir());
            this.ftpEnabled.setSelected(Main.config.getFtpEnabled());
            this.ftpPassw.setText(Main.config.getFtpPass());
            this.ftpPort.setText(Main.config.getFtpPort() + "");
            this.ftpUser.setText(Main.config.getFtpUser());
            this.ftpWeburl.setText(Main.config.getFtpWebUrl());
            this.srvAddr.setText(Main.config.getIp());
            this.srvPassw.setText(Main.config.getPass());
            this.srvPort.setText(Main.config.getPort() + "");
            this.saveEnabled.setSelected(Main.config.isSaveEnabled());
            this.saveDir = new JFileChooser();
            this.saveDir.setSelectedFile(new File(Main.config.getSaveDir()));
            this.ftpesEnabled.setSelected(Main.config.getFtpesEnabled());
            this.acceptCertificates.setSelected(Main.config.getAcceptAllCertificates());
            this.startUpEnabled.setSelected(Main.config.isStartUpEnabled());
        } catch (Exception e) {
            e.printStackTrace();
            Main.dialog.show("Config error", "Error during the config loading", false);
            Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }

        update();
    }

    private void update() {
        if (!this.ftpEnabled.isSelected()) {
            this.ftpAddr.setEnabled(false);
            this.ftpDir.setEnabled(false);
            this.ftpPassw.setEnabled(false);
            this.ftpPort.setEnabled(false);
            this.ftpUser.setEnabled(false);
            this.ftpWeburl.setEnabled(false);
            this.acceptCertificates.setEnabled(false);
            this.ftpesEnabled.setEnabled(false);

            this.srvAddr.setEnabled(true);
            this.srvPassw.setEnabled(true);
            this.srvPort.setEnabled(true);
            Main.myLog("[Settings] Server enabled, ftp disabled");

        } else {
            this.ftpAddr.setEnabled(true);
            this.ftpDir.setEnabled(true);
            this.ftpPassw.setEnabled(true);
            this.ftpPort.setEnabled(true);
            this.ftpUser.setEnabled(true);
            this.ftpWeburl.setEnabled(true);
            this.ftpesEnabled.setEnabled(true);

            this.srvAddr.setEnabled(false);
            this.srvPassw.setEnabled(false);
            this.srvPort.setEnabled(false);
            Main.myLog("[Settings] Server disabled, ftp enabled");
        }
    }

    /*
     * LISTENER
     */
    JButton callerBtn;

    String newKey;
    HashSet<Integer> hashKeyGlobal = new HashSet<>();
    boolean hash1Ready = false;
    boolean hash2Ready = false;

    protected void enableListener(JButton btn) {
        System.out.println("[SettingsDialog] Listener enabled");
        newKey = "";
        hashKeyGlobal.clear();
        hash1Ready = false;
        hash2Ready = false;
        this.callerBtn = btn;
        Main.keyListener.disableListener();
        GlobalScreen.addNativeKeyListener(this);
    }

    protected void disableListener(JButton btn) {
        System.out.println("[SettingsDialog] Listener disabled");
        GlobalScreen.removeNativeKeyListener(this);
        Main.keyListener.enableListener();
        btn.setEnabled(true);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent arg0) {

        System.out.println("[SettingsDialog] Pressed: " + arg0.getKeyCode());

        if (hash2Ready && !hashKeyGlobal.contains(arg0.getKeyCode())
                && MyKeyListener.fromKeyToName.containsKey(arg0.getKeyCode())) {
            hashKeyGlobal.add(arg0.getKeyCode());
            newKey += "+" + String.valueOf(arg0.getKeyCode());
            callerBtn.setText(callerBtn.getText() + "+" + MyKeyListener.fromKeyToName.get(arg0.getKeyCode()));
            System.out.println("[MyKeyListener] Max key combination reached");
        }

        if ((hash1Ready) && !hashKeyGlobal.contains(arg0.getKeyCode())
                && MyKeyListener.fromKeyToName.containsKey(arg0.getKeyCode())) {
            myLog("[MyKeyListener] Combination received");
            hashKeyGlobal.add(arg0.getKeyCode());
            newKey += "+" + String.valueOf(arg0.getKeyCode());
            callerBtn.setText(callerBtn.getText() + "+" + MyKeyListener.fromKeyToName.get(arg0.getKeyCode()));
            hash2Ready = true;
        }

        if (!hashKeyGlobal.contains(arg0.getKeyCode()) && MyKeyListener.fromKeyToName.containsKey(arg0.getKeyCode())) {
            hashKeyGlobal.add(arg0.getKeyCode());
            newKey += String.valueOf(arg0.getKeyCode());
            callerBtn.setText(MyKeyListener.fromKeyToName.get(arg0.getKeyCode()));
            hash1Ready = true;
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent arg0) {
        if ((hash1Ready || hash2Ready)) {
            disableListener(callerBtn);

            if (callerBtn.equals(btnScreen)) {
                Main.config.setScreenKeys(newKey);
            } else if (callerBtn.equals(btnCScreen)) {
                Main.config.setCScreenKeys(newKey);
            } else if (callerBtn.equals(btnFile)) {
                Main.config.setFileKeys(newKey);
            } else if (callerBtn.equals(btnClipboard)) {
                Main.config.setClipboardKeys(newKey);
            }
            Main.st.updateKeys();
            Main.keyListener.loadKeys();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent arg0) {
    }
}