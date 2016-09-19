using System;
using System.Windows.Forms;

namespace KSLUploader
{
    public class SystemTray
    {
        private NotifyIcon trayIcon;

        public SystemTray()
        {
            trayIcon = new NotifyIcon();
            trayIcon.Text = "KSLUploader";
            trayIcon.Icon = Properties.Resources.AppIcon;
            trayIcon.MouseDoubleClick += onDoubleClick;

            trayIcon.ContextMenuStrip = buildMenu();

            trayIcon.Visible = true;
        }

        private ContextMenuStrip buildMenu()
        {
            ContextMenuStrip trayMenu = new ContextMenuStrip();
            trayMenu.Items.Add("KSLUploader v0.0.2 Beta", null, null);
            trayMenu.Items.Add("Recent Items", null);

            trayMenu.Items.Add("-");
            trayMenu.Items.Add("-");

            trayMenu.Items.Add("Capture Area", null);
            trayMenu.Items.Add("Capture Desktop", null);
            trayMenu.Items.Add("Upload File", null);
            trayMenu.Items.Add("Upload Clipboard", null);

            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Settings");
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Quit", KSLUploader.Properties.Resources.Quit, Quit) ;

            //disable first and second element in menu
            trayMenu.Items[0].Enabled = false;
            trayMenu.Items[1].Enabled = false;

            return trayMenu;
        }

        private void Quit(object sender, EventArgs e)
        {
            trayIcon.Dispose();
            App.ExitApplication();
        }

        private void onDoubleClick(object sender, MouseEventArgs e)
        {
            Console.Write("HELLO WORLD!");
        }
    }
}
