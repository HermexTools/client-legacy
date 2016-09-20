using System;
using System.Diagnostics;
using System.Windows.Forms;
using KSLUploader.Windows;

namespace KSLUploader.Classes
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
            trayMenu.Items.Add("Capture Desktop", Properties.Resources.Desktop, delegate { CaptureDesktop(); });
            trayMenu.Items.Add("Upload File", Properties.Resources.File);
            trayMenu.Items.Add("Upload Clipboard", Properties.Resources.Clipboard);
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Settings", Properties.Resources.Settings, SettingsEvent);
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Quit", Properties.Resources.Quit, QuitEvent);

            //return menu
            return trayMenu;
        }



        #region SYSTEMTRAY EVENTS

        private void SettingsEvent(object sender, EventArgs e)
        {
            if(!App.IsWindowOpen<Settings>())
            {
                var settingsWindow = new Settings();
                settingsWindow.Show();
            }
        }

        private void QuitEvent(object sender, EventArgs e)
        {
            trayIcon.Dispose();
            App.ExitApplication();
        }

        private void onDoubleClick(object sender, MouseEventArgs e)
        {
            CaptureDesktop();
        }

        #endregion

        #region PROGRAM EVENTS

        private void CaptureDesktop()
        {
            Screenshot.CaptureDesktop();
        }

        #endregion
    }
}
