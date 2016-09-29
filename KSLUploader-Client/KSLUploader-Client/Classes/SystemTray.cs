using KSLUploader.Classes.Uploaders;
using KSLUploader.Windows;
using System;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace KSLUploader.Classes
{
    public class SystemTray
    {
        private NotifyIcon trayIcon;

        public SystemTray()
        {
            trayIcon = new NotifyIcon();

            //trayicon name
            trayIcon.Text = AppConstants.Name;

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
            trayMenu.Items.Add(AppConstants.Name + " v" + AppConstants.Version, null, null).Enabled = false;
            trayMenu.Items.Add("-");
            trayMenu.Items.Add(recentItems);
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Capture Area", Properties.Resources.Area, delegate { CaptureArea(); });
            if (Screen.AllScreens.Length > 1)
            {
                //sub menu -> desktops
                ToolStripMenuItem desktopItems = new ToolStripMenuItem("Capture Desktop", Properties.Resources.Desktop);
                desktopItems.DropDownItems.Add("All Desktops", null, delegate { CaptureDesktop(null); });
                desktopItems.DropDownItems.Add("-");
                int i = 1;
                foreach (var screen in Screen.AllScreens)
                {
                    string monitor = i + ". " + screen.Bounds.Width + "x" + screen.Bounds.Height;

                    desktopItems.DropDownItems.Add(monitor, null, delegate { CaptureDesktop(screen); });
                    i++;
                }

                trayMenu.Items.Add(desktopItems);
            }
            else
            {
                trayMenu.Items.Add("Capture Desktop", Properties.Resources.Desktop, delegate { CaptureDesktop(null); });
            }
            trayMenu.Items.Add("Upload File", Properties.Resources.File, delegate { UploadFile(); });
            trayMenu.Items.Add("Upload Clipboard", Properties.Resources.Clipboard, delegate { UploadClipboard(); });
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Settings", Properties.Resources.Settings, delegate { ShowSettings(); });
            trayMenu.Items.Add("-");
            trayMenu.Items.Add("Quit", Properties.Resources.Quit, delegate { Exit(); });

            //return menu
            return trayMenu;
        }

        #region SYSTEMTRAY EVENTS

        private void onDoubleClick(object sender, MouseEventArgs e)
        {
            new UploadsPanel().Show();
        }

        private void ShowProgress(object sender, ProgressChangedEventArgs e)
        {
            Graphics canvas;
            Bitmap iconBitmap = new Bitmap(16, 16);
            canvas = Graphics.FromImage(iconBitmap);

            canvas.DrawIcon(Properties.Resources.AppIcon, 0, 0);

            StringFormat format = new StringFormat();
            format.Alignment = StringAlignment.Center;

            canvas.DrawString(
                e.ProgressPercentage.ToString(),
                new Font("Calibri", 8, FontStyle.Bold),
                new SolidBrush(Color.White),
                new RectangleF(0, 3, 16, 13),
                format
            );

            trayIcon.Icon = Icon.FromHandle(iconBitmap.GetHicon());
        }

        #endregion

        #region PROGRAM EVENTS
        private void CaptureDesktop(Screen s)
        {
            FileInfo screen;
            if (s != null)
            {
                screen = Screenshot.CaptureDesktop(s);
            }
            else
            {
                screen = Screenshot.CaptureDesktop();
            }

            BackgroundWorker b = new BackgroundWorker();
            b.WorkerReportsProgress = true;
            b.ProgressChanged += ShowProgress;

            SocketUploader upload = new SocketUploader(screen, b, "{0}.png");
            if (upload.Upload())
            {
                trayIcon.ShowBalloonTip(5000, "Upload Completed", upload.Link, ToolTipIcon.Info);
            }
        }

        private void CaptureArea()
        {
            var captureWin = new CaptureWindow();
            if (captureWin.hasCaught())
            {
                FileInfo screen = Screenshot.CaptureArea(
                    Screenshot.FromWindowsToDrawingPoint(captureWin.StartPoint),
                    Screenshot.FromWindowsToDrawingPoint(captureWin.EndPoint)
                );

                BackgroundWorker b = new BackgroundWorker();
                b.WorkerReportsProgress = true;
                b.ProgressChanged += ShowProgress;

                SocketUploader upload = new SocketUploader(screen, b, "{0}.png");
                if (upload.Upload())
                {
                    trayIcon.ShowBalloonTip(5000, "Upload Completed", upload.Link, ToolTipIcon.Info);
                }
            }
            else
            {
                trayIcon.ShowBalloonTip(2000, "Upload Cancelled", "", ToolTipIcon.Info);
            }
        }

        private void UploadFile()
        {

        }

        private void UploadClipboard()
        {
            string clipboard = ClipboardManager.GetText();
            if(clipboard!=null)
            {
                //save in temp to send
                FileInfo textFile = new FileInfo(AppConstants.SaveTextToTempName);

                if (!File.Exists(textFile.FullName))
                {
                    using (var fs = File.Create(textFile.FullName))
                    {
                        fs.Close();
                    }
                }

                File.WriteAllText(textFile.FullName, clipboard);
            }
        }

        private void Exit()
        {
            trayIcon.Dispose();
            App.Current.Shutdown();
        }

        private void ShowSettings()
        {
            if (!Utils.IsWindowOpen<Settings>())
            {
                new Settings().Show();
            }
        }

        #endregion

    }
}
