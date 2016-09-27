using KSLUploader.Classes;
using System.Diagnostics;
using System.Linq;
using System.Windows;
using System;
using Microsoft.Win32;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;

namespace KSLUploader
{
    public partial class App : Application
    {
        private SystemTray ksluTray;
        private KeyListener keyListener;

        protected override void OnStartup(StartupEventArgs e)
        {
            Logger.Set("App started", LogType.INFO);

            //no double instance!
            if(Process.GetProcessesByName(Process.GetCurrentProcess().ProcessName).Length > 1)
            {
                MessageBox.Show("KSLUploader is already running.", "Error");
                Current.Shutdown();
                return;
            }
           
            Current.ShutdownMode = ShutdownMode.OnExplicitShutdown;

            InizializeSettings();
            Utils.CheckRunAtStartup();
            
            keyListener = new KeyListener();
            ksluTray = new SystemTray();
        }

        private void InizializeSettings()
        {
            //generals
            SettingsManager.Inizialize("RunAtStartup", false);
            SettingsManager.Inizialize("SaveLocal", false);
            SettingsManager.Inizialize("SaveLocalPath", Environment.GetFolderPath(Environment.SpecialFolder.MyPictures));
            SettingsManager.Inizialize("UploadMethod", "SOCKET");

            //protocol
            SettingsManager.Inizialize("SocketAddress","localhost");
            SettingsManager.Inizialize("SocketPort", 4030);
            SettingsManager.Inizialize("SocketPassword", "pass");

            //ftp
            SettingsManager.Inizialize("UseFTPS", false);
            SettingsManager.Inizialize("AcceptCertificates", true);
            SettingsManager.Inizialize("FTPAddress", null);
            SettingsManager.Inizialize("FTPPort", 21);
            SettingsManager.Inizialize("FTPDirectory", "/");
            SettingsManager.Inizialize("FTPWeburl", null);
            SettingsManager.Inizialize("FTPUser", null);
            SettingsManager.Inizialize("FTPPassword", null);

            //shortcut
            SettingsManager.Inizialize("ShortcutArea", new List<int>() { 162, 160, 49 });
            SettingsManager.Inizialize("ShortcutDesktop", new List<int>() { 162, 160, 50 });
            SettingsManager.Inizialize("ShortcutFile", new List<int>() { 162, 160, 51 });
            SettingsManager.Inizialize("ShortcutClipboard", new List<int>() { 162, 160, 52 });
        }

        


    }
}
