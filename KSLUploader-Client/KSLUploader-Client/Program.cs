using System;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using KSLUploader_Client.Tools;
using KSLUploader_Client.Windows;

namespace KSLUploader_Client
{
    public static class Program
    {
        //application entry point
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new ProgramContext());
        }
    }

    public class ProgramContext : ApplicationContext
    {
        private NotifyIcon trayIcon;

        public ProgramContext()
        {
            //tray icon initialization
            trayIcon = new NotifyIcon();

            //tray icon name
            trayIcon.Text = "KSLU";

            //tray icon image
            trayIcon.Icon = Icon.FromHandle(Properties.Resources.AppIcon.GetHicon());

            //tray icon context menu
            ContextMenuStrip smenu = new ContextMenuStrip();
            smenu.Items.Add("KSLU v0.0.1 Beta", null, null);
            smenu.Items.Add("Debug", null, DebugEvent);
            smenu.Items.Add("-");

            smenu.Items.Add("Capture Desktop", null, CaptureDesktopEvent);

            smenu.Items.Add("-");
            smenu.Items.Add("Settings", Properties.Resources.Settings, Settings);
            smenu.Items.Add("-");
            smenu.Items.Add("Quit", Properties.Resources.Quit, Quit);

            //disable first element in menu
            smenu.Items[0].Enabled = false;

            //add context menu on tray icon
            trayIcon.ContextMenuStrip = smenu;

            //tray icon double click event
            trayIcon.MouseDoubleClick += TrayIconDoubleClick;

            //show the tray icon
            trayIcon.Visible = true;
        }
        
        #region TRAYICON EVENTS
        
        private void TrayIconDoubleClick(object sender, MouseEventArgs e)
        {
            CaptureDesktop();
        }

        #endregion

        #region CONTEXTMENU EVENTS

        private void DebugEvent(object sender, EventArgs e)
        {
            StringBuilder mystring = new StringBuilder();
            mystring.AppendLine("use_ftp: " + Properties.Settings.Default.use_ftp.ToString());
            mystring.AppendLine("use_ftps: " + Properties.Settings.Default.use_ftps.ToString());
            mystring.AppendLine("save_image: " + Properties.Settings.Default.save_image.ToString());
            mystring.AppendLine("open_startup: " + Properties.Settings.Default.open_startup.ToString());
            mystring.AppendLine();
            mystring.AppendLine("ftp_address: " + Properties.Settings.Default.ftp_address.ToString());
            mystring.AppendLine("ftp_port: " + Properties.Settings.Default.ftp_port.ToString());
            mystring.AppendLine("ftp_directory: " + Properties.Settings.Default.ftp_directory.ToString());
            mystring.AppendLine("ftp_weburl: " + Properties.Settings.Default.ftp_weburl.ToString());
            mystring.AppendLine("ftp_user: " + Properties.Settings.Default.ftp_user.ToString());
            mystring.AppendLine("ftp_password: " + Properties.Settings.Default.ftp_password.ToString());
            mystring.AppendLine("ftp_certificate: " + Properties.Settings.Default.ftp_certificate.ToString());
            mystring.AppendLine();
            mystring.AppendLine("server_address: " + Properties.Settings.Default.server_address.ToString());
            mystring.AppendLine("server_password: " + Properties.Settings.Default.server_password.ToString());
            mystring.AppendLine("server_port: " + Properties.Settings.Default.server_port.ToString());
            mystring.AppendLine();
            mystring.AppendLine("shortcut_area: " + Properties.Settings.Default.shortcut_area.ToString());
            mystring.AppendLine("shortcut_desktop: " + Properties.Settings.Default.shortcut_desktop.ToString());
            mystring.AppendLine("shortcut_file: " + Properties.Settings.Default.shortcut_file.ToString());
            mystring.AppendLine("shortcut_clipboard: " + Properties.Settings.Default.shortcut_clipboard.ToString());


            MessageBox.Show(mystring.ToString(), "SETTINGS DEBUG");
        }

        private void CaptureDesktopEvent(object sender, EventArgs e)
        {
            CaptureDesktop();
        }

        private void Settings(object sender, EventArgs e)
        {
            if(!Utilities.CheckFormIsOpened("Settings"))
            {
                //open the settings window
                new Settings().ShowDialog();
            }
        }

        private void Quit(object sender, EventArgs e)
        {
            //cleanup so that the icon will be removed when the application is closed
            trayIcon.Visible = false;

            //close app
            Application.Exit();
        }

        #endregion

        #region PROGRAM EVENTS

        private void CaptureDesktop()
        {
            //Thread.Sleep(250);
            

        }

        #endregion
    }
}
