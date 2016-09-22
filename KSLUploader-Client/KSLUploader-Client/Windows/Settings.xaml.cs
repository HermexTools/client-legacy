using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Media;
using KSLUploader.Classes;

namespace KSLUploader.Windows
{
    /// <summary>
    /// Logica di interazione per Settings.xaml
    /// </summary>
    public partial class Settings : Window
    {
        public Settings()
        {
            InitializeComponent();

            //generals
            generals_startup.IsChecked = (bool)SettingsManager.Get("RunAtStartup");
            generals_save.IsChecked = (bool)SettingsManager.Get("SaveLocal");
            generals_browselabel.Text=SettingsManager.Get("SaveLocalPath")==null?"Not set.": (string)SettingsManager.Get("SaveLocalPath");
            generals_method_socket.IsChecked = (string)SettingsManager.Get("UploadMethod") == "SOCKET" ? true : false;
            generals_method_ftp.IsChecked = (string)SettingsManager.Get("UploadMethod") == "FTP" ? true : false;

            //protocol
            protocol_address.Text= (string)SettingsManager.Get("SocketAddress");
            protocol_port.Text = Convert.ToInt32(SettingsManager.Get("SocketPort")).ToString();
            protocol_password.Text = (string)SettingsManager.Get("SocketPassword");

            //ftp
            ftp_useftps.IsChecked= (bool)SettingsManager.Get("UseFTPS");
            ftp_certificates.IsChecked= (bool)SettingsManager.Get("AcceptCertificates");
            ftp_address.Text= (string)SettingsManager.Get("FTPAddress");
            ftp_port.Text= Convert.ToInt32(SettingsManager.Get("FTPPort")).ToString();
            ftp_directory.Text= (string)SettingsManager.Get("FTPDirectory");
            ftp_weburl.Text= (string)SettingsManager.Get("FTPWeburl");
            ftp_user.Text= (string)SettingsManager.Get("FTPUser");
            ftp_password.Text= (string)SettingsManager.Get("FTPPassword");

            //shortcut
            shortcut_area.Content= (string)SettingsManager.Get("ShortcutArea");
            shortcut_desktop.Content = (string)SettingsManager.Get("ShortcutDesktop");
            shortcut_file.Content = (string)SettingsManager.Get("ShortcutFile");
            shortcut_clipboard.Content = (string)SettingsManager.Get("ShortcutClipboard");

            //events
            CheckSaveLocalImage();
            CheckUploadMethod();
            generals_save.Click += (s, e) => { CheckSaveLocalImage(); };
            generals_browsebutton.Click += Generals_browsebutton_Click;
            generals_method_socket.Click += (s, e) => { CheckUploadMethod(); };
            generals_method_ftp.Click += (s, e) => { CheckUploadMethod(); };

            saveButton.Click += SaveButton_Click;
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
                generals_browselabel.Foreground = new SolidColorBrush(Colors.Black);
            }
            else
            {
                generals_browsebutton.IsEnabled = false;
                generals_browselabel.Foreground = new SolidColorBrush(Colors.Gray);
            }
        }

        private void Generals_browsebutton_Click(object sender, RoutedEventArgs e)
        {
            var dialog = new FolderBrowserDialog();
            dialog.Description = "Please select the path of save folder.";
            var result = dialog.ShowDialog();
            if(result==System.Windows.Forms.DialogResult.OK)
            {
                generals_browselabel.Text = dialog.SelectedPath;
            }            
        }

        private void SaveButton_Click(object sender, RoutedEventArgs e)
        {
            //generals
            SettingsManager.Set("RunAtStartup", generals_startup.IsChecked);
            SettingsManager.Set("SaveLocal", generals_save.IsChecked);
            SettingsManager.Set("SaveLocalPath", generals_browselabel.Text);
            SettingsManager.Set("UploadMethod", generals_method_socket.IsChecked==true?"SOCKET":"FTP");

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
            SettingsManager.Set("ShortcutArea", "CTRL+SHIFTSX+1");
            SettingsManager.Set("ShortcutDesktop", "CTRL+SHIFTSX+2");
            SettingsManager.Set("ShortcutFile", "CTRL+SHIFTSX+3");
            SettingsManager.Set("ShortcutClipboard", "CTRL+SHIFTSX+4");

            //close window
            this.Close();
        }
    }
}
