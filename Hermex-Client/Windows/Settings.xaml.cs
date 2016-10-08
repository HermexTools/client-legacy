using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Hermex.Classes;

namespace Hermex.Windows
{
    public partial class Settings : Window
    {
        private NewKeyListener SettingsKeyListener = new NewKeyListener();

        private HashSet<int> ShortcutAreaSetting = new HashSet<int>();
        private HashSet<int> ShortcutDesktopSetting = new HashSet<int>();
        private HashSet<int> ShortcutFileSetting = new HashSet<int>();
        private HashSet<int> ShortcutClipboardSetting = new HashSet<int>();

        private Button CurrentShortcutButton = null;

        public Settings()
        {
            InitializeComponent();

            //center window
            this.WindowStartupLocation = WindowStartupLocation.CenterScreen;

            //generals
            generals_startup.IsChecked = SettingsManager.Get<bool>("RunAtStartup");
            generals_save.IsChecked = SettingsManager.Get<bool>("SaveLocal");
            generals_browsepath.Text = SettingsManager.Get<string>("SaveLocalPath") == null ? "Not set." : SettingsManager.Get<string>("SaveLocalPath");
            generals_method_socket.IsChecked = SettingsManager.Get<string>("UploadMethod") == "SOCKET" ? true : false;
            generals_method_ftp.IsChecked = SettingsManager.Get<string>("UploadMethod") == "FTP" ? true : false;

            //protocol
            protocol_address.Text = SettingsManager.Get<string>("SocketAddress");
            protocol_port.Text = SettingsManager.Get<int>("SocketPort").ToString();
            protocol_password.Text = SettingsManager.Get<string>("SocketPassword");

            //ftp
            ftp_useftps.IsChecked = SettingsManager.Get<bool>("UseFTPS");
            ftp_certificates.IsChecked = SettingsManager.Get<bool>("AcceptCertificates");
            ftp_address.Text = SettingsManager.Get<string>("FTPAddress");
            ftp_port.Text = SettingsManager.Get<int>("FTPPort").ToString();
            ftp_directory.Text = SettingsManager.Get<string>("FTPDirectory");
            ftp_weburl.Text = SettingsManager.Get<string>("FTPWeburl");
            ftp_user.Text = SettingsManager.Get<string>("FTPUser");
            ftp_password.Text = SettingsManager.Get<string>("FTPPassword");

            //shortcut
            ShortcutAreaSetting = SettingsManager.Get<HashSet<int>>("ShortcutArea");
            ShortcutDesktopSetting = SettingsManager.Get<HashSet<int>>("ShortcutDesktop");
            ShortcutFileSetting = SettingsManager.Get<HashSet<int>>("ShortcutFile");
            ShortcutClipboardSetting = SettingsManager.Get<HashSet<int>>("ShortcutClipboard");
            
            shortcut_area.Content = Utils.GetStringCombination(ShortcutAreaSetting);
            shortcut_desktop.Content = Utils.GetStringCombination(ShortcutDesktopSetting);
            shortcut_file.Content = Utils.GetStringCombination(ShortcutFileSetting);
            shortcut_clipboard.Content = Utils.GetStringCombination(ShortcutClipboardSetting);

            //events
            CheckSaveLocalImage();
            CheckUploadMethod();

            generals_save.Click += (s, e) => { CheckSaveLocalImage(); };
            generals_browsebutton.Click += Generals_browsebutton_Click;
            generals_method_socket.Click += (s, e) => { CheckUploadMethod(); };
            generals_method_ftp.Click += (s, e) => { CheckUploadMethod(); };

            shortcut_area.Click += ShortcutClick;
            shortcut_desktop.Click += ShortcutClick;
            shortcut_file.Click += ShortcutClick;
            shortcut_clipboard.Click += ShortcutClick;

            saveButton.Click += SaveButton_Click;

            //info
            info_title.Text = AppConstants.Name;
            info_developers.Text = "Developed by " + String.Join(", ", AppConstants.Developers);
            info_version.Text = "Version: " + AppConstants.Version;

            SettingsKeyListener.OnKeyPressed += SettingsKeyListener_OnKeyPressed;
            SettingsKeyListener.OnCombinationCompleted += SettingsKeyListener_OnCombinationCompleted;
        }

        private void SettingsKeyListener_OnCombinationCompleted(object sender, KeyListenerEventArgs e)
        {
            CurrentShortcutButton.Content = Utils.GetStringCombination(e.Combination);
            switch(CurrentShortcutButton.Name.Split('_')[1])
            {
                case "area":
                    ShortcutAreaSetting.Clear();
                    ShortcutAreaSetting.UnionWith(e.Combination);
                    break;
                case "desktop":
                    ShortcutDesktopSetting.Clear();
                    ShortcutDesktopSetting.UnionWith(e.Combination);
                    break;
                case "file":
                    ShortcutFileSetting.Clear();
                    ShortcutFileSetting.UnionWith(e.Combination);
                    break;
                case "clipboard":
                    ShortcutClipboardSetting.Clear();
                    ShortcutClipboardSetting.UnionWith(e.Combination);
                    break;
            }

            CurrentShortcutButton.IsEnabled = true;
            CurrentShortcutButton = null;
            SettingsKeyListener.DisableListener();
            (App.Current as App).GlobalKeyListener.EnableListener();
        }

        private void SettingsKeyListener_OnKeyPressed(object sender, KeyListenerEventArgs e)
        {
            CurrentShortcutButton.Content = Utils.GetStringCombination(e.Combination);
        }

        private void ShortcutClick(object sender, RoutedEventArgs e)
        {
            CurrentShortcutButton = sender as Button;

            (App.Current as App).GlobalKeyListener.DisableListener();

            CurrentShortcutButton.Content = "Select a hotkey...";
            CurrentShortcutButton.IsEnabled = false;
            SettingsKeyListener.EnableListener();
        }

        private void CheckUploadMethod()
        {
            if(generals_method_socket.IsChecked==true)
            {
                tab_protocol.IsEnabled = true;
                tab_ftp.IsEnabled = false;
            }
            else
            {
                tab_protocol.IsEnabled = false;
                tab_ftp.IsEnabled = true;
            }
        }

        private void CheckSaveLocalImage()
        {
            if(generals_save.IsChecked==true)
            {
                generals_browsebutton.IsEnabled = true;
                generals_browsepath.IsEnabled = true;
            }
            else
            {
                generals_browsebutton.IsEnabled = false;
                generals_browsepath.IsEnabled = false;
            }
        }

        private void Generals_browsebutton_Click(object sender, RoutedEventArgs e)
        {
            var dialog = new System.Windows.Forms.FolderBrowserDialog();
            dialog.Description = "Please select the path of save folder.";
            var result = dialog.ShowDialog();
            if(result==System.Windows.Forms.DialogResult.OK)
            {
                generals_browsepath.Text = dialog.SelectedPath;
            }            
        }

        private void SaveButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                //generals
                SettingsManager.Set("RunAtStartup", generals_startup.IsChecked);
                SettingsManager.Set("SaveLocal", generals_save.IsChecked);
                SettingsManager.Set("SaveLocalPath", generals_browsepath.Text);
                SettingsManager.Set("UploadMethod", generals_method_socket.IsChecked == true ? "SOCKET" : "FTP");

                //protocol
                SettingsManager.Set("SocketAddress", protocol_address.Text);
                SettingsManager.Set("SocketPort", Convert.ToInt32(protocol_port.Text));
                SettingsManager.Set("SocketPassword", protocol_password.Text);

                //ftp
                SettingsManager.Set("UseFTPS", ftp_useftps.IsChecked);
                SettingsManager.Set("AcceptCertificates", ftp_certificates.IsChecked);
                SettingsManager.Set("FTPAddress", ftp_address.Text);
                SettingsManager.Set("FTPPort", Convert.ToInt32(ftp_port.Text));
                SettingsManager.Set("FTPDirectory", ftp_directory.Text);
                SettingsManager.Set("FTPWeburl", ftp_weburl.Text);
                SettingsManager.Set("FTPUser", ftp_user.Text);
                SettingsManager.Set("FTPPassword", ftp_password.Text);
                                
                //shortcut
                SettingsManager.Set("ShortcutArea", ShortcutAreaSetting);
                SettingsManager.Set("ShortcutDesktop", ShortcutDesktopSetting);
                SettingsManager.Set("ShortcutFile", ShortcutFileSetting);
                SettingsManager.Set("ShortcutClipboard", ShortcutClipboardSetting);

                //check startup
                Utils.CheckRunAtStartup();

                //save config
                SettingsManager.SaveSettingsFile();

                //close window
                this.Close();
            }
            catch(Exception ex)
            {
                switch(ex.HResult)
                {
                    case -2146233033:
                        MessageBox.Show("Socket Port and FTP Port must be integer!","Warning");
                        break;

                    default:
                        Debug.WriteLine(ex.Message + "\n" + ex.StackTrace + "\n" + ex.HResult);
                        break;
                }
            }
        }
    }
}
