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

            //trayicon name
            trayIcon.Text = "KSLUploader";

            //trayicon icon
            trayIcon.Icon = Properties.Resources.AppIcon;

            //trayicon doubleclick event
            trayIcon.MouseDoubleClick += onDoubleClick;

            //trayicon menu
            trayIcon.ContextMenuStrip = buildMenu();

            //show trayicon
            trayIcon.Visible = true;
        }

        private ContextMenuStrip buildMenu()
        {
            //sub menu -> recent items
            ToolStripMenuItem recentItems = new ToolStripMenuItem("Recent Items");
            recentItems.DropDownItems.Add("No items").Enabled = false;

            //main menu
            ContextMenuStrip trayMenu = new ContextMenuStrip();
            trayMenu.Items.Add("KSLUploader v0.0.2 Beta", null, null).Enabled = false;
            trayMenu.Items.Add("-");
            trayMenu.Items.Add(recentItems);            
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Capture Area", Properties.Resources.Area);
            trayMenu.Items.Add("Capture Desktop", Properties.Resources.Desktop);
            trayMenu.Items.Add("Upload File", Properties.Resources.File);
            trayMenu.Items.Add("Upload Clipboard", Properties.Resources.Clipboard);
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Settings", Properties.Resources.Settings);
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Quit", Properties.Resources.Quit, Quit);

            //return menu
            return trayMenu;
        }

        #region SYSTEMTRAY EVENTS

        private void Quit(object sender, EventArgs e)
        {
            trayIcon.Dispose();
            App.ExitApplication();
        }

        private void onDoubleClick(object sender, MouseEventArgs e)
        {
            Console.Write("HELLO WORLD!");
        }

        #endregion
    }
}
