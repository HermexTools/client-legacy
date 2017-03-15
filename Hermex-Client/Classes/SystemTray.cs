using Hermex.Classes.Uploaders;
using Hermex.Windows;
using System;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

namespace Hermex.Classes
{
    public class SystemTray
    {
        private NotifyIcon tray;
        private App GlobalEnvironment = App.Current as App;

        public SystemTray()
        {
            tray = new NotifyIcon();

            //tray name
            tray.Text = AppConstants.Name;

            //tray icon
            tray.Icon = Properties.Resources.AppIcon;

            //tray events
            tray.MouseClick += OnClick; //for testing only
            tray.MouseDoubleClick += OnDoubleClick;
            tray.BalloonTipClicked += OnTooltipClick;

            //tray menu
            tray.ContextMenuStrip = buildMenu();

            //show tray 
            tray.Visible = true;

            //global keylistener shortcut event
            GlobalEnvironment.GlobalKeyListener.OnShortcutEvent += OnShortcutEvent;
        }
        
        //execute functions from shortcuts
        private void OnShortcutEvent(object sender, ShortcutEventArgs e)
        {
            switch (e.shortcutEvent)
            {
                case ShortcutEvent.ShortcutArea:
                    CaptureArea();
                    break;
                case ShortcutEvent.ShortcutDesktop:
                    CaptureDesktop(null);
                    break;
                case ShortcutEvent.ShortcutFile:
                    UploadFile();
                    break;
                case ShortcutEvent.ShortcutClipboard:
                    UploadClipboard();
                    break;
            }
        }

        private ContextMenuStrip buildMenu()
        {
            //sub menu -> recent items
            ToolStripMenuItem recentItems = new ToolStripMenuItem("Recent Items");
            var recent = AppSettings.Get<Queue<string>>("RecentItems");
            if(recent.Count==0)
            {
                recentItems.DropDownItems.Add("No items").Enabled = false;
            }
            else
            {
                foreach(string item in recent.Reverse())
                {
                    recentItems.DropDownItems.Add(item).Click+=(s,e)=>
                    {
                        var dropdownitem = (s as ToolStripItem);
                        if(dropdownitem.Enabled && !string.IsNullOrEmpty(dropdownitem.Text) && Utils.CheckURL(dropdownitem.Text))
                        {
                            Process.Start(dropdownitem.Text);
                        }                        
                    };
                }
            }
            

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
            trayMenu.Items.Add("Exit", Properties.Resources.Quit, delegate { Exit(); });

            //return menu
            return trayMenu;
        }

        private void SetRecentItem(string url)
        {
            var recent = AppSettings.Get<Queue<string>>("RecentItems");
            recent.Enqueue(url);

            if(recent.Count>10)
            {
                recent.Dequeue();
            }

            AppSettings.Set("RecentItems", recent);
            tray.ContextMenuStrip = buildMenu();
        }

        #region SYSTEMTRAY EVENTS
        int i = 0;
        private void OnClick(object sender, MouseEventArgs e)
        {
            if(e.Button == MouseButtons.Left)
            {
                i++;
                SetRecentItem("test " + i);
            }
        }

        private void OnDoubleClick(object sender, MouseEventArgs e)
        {
            if (!Utils.IsWindowOpen<Settings>())
            {
                new Settings().Show();
            }
        }

        //if text is a correct url, open it in browser
        private void OnTooltipClick(object sender, EventArgs e)
        {
            string text = tray.BalloonTipText;

            if(!string.IsNullOrEmpty(text) && Utils.CheckURL(text))
            {
                Process.Start(text);
            }
        }

        private void ShowProgress(object sender, int progress)
        {
            Graphics canvas;
            Bitmap iconBitmap = new Bitmap(32, 32);
            canvas = Graphics.FromImage(iconBitmap);

            canvas.DrawIcon(Properties.Resources.LoadingIcon, 0, 0);
            
            canvas.DrawString(
                progress < 100 ? progress.ToString() : "99",
                new Font("Segoe UI", 13, FontStyle.Bold),
                new SolidBrush(Color.White),
                new RectangleF(0, 3, 32, 29),
                new StringFormat() { Alignment=StringAlignment.Center }
            );

            tray.Icon = Icon.FromHandle(iconBitmap.GetHicon());
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


            Progress<int> progressReporter = new Progress<int>();
            progressReporter.ProgressChanged += ShowProgress;

            SocketUploader upload = new SocketUploader(screen, progressReporter, "{0}.png");
            if (upload.Upload())
            {
                tray.ShowBalloonTip(5000, "Upload Completed", upload.Link, ToolTipIcon.Info);  
            }

            tray.Icon = Properties.Resources.AppIcon;
            //trayIcon.Icon = new Icon(this.GetType(), "AppIcon.ico");

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


                Progress<int> progressReporter = new Progress<int>();
                progressReporter.ProgressChanged += ShowProgress;

                SocketUploader upload = new SocketUploader(screen, progressReporter, "{0}.png");
                if (upload.Upload())
                {
                    Utils.SetTooltip(tray, 5000, "Upload Completed", upload.Link, ToolTipIcon.Info);
                    ClipboardManager.SetText(upload.Link);
                }

                tray.Icon = Properties.Resources.AppIcon;
            }
            else
            {
                Utils.SetTooltip(tray, 2000, "Upload Cancelled", "", ToolTipIcon.Info);
            }
        }

        private void UploadFile()
        {
            var dialog = new OpenFileDialog();
            dialog.Multiselect = true;
            if (dialog.ShowDialog() == DialogResult.OK)
            {

            }
        }

        private void UploadClipboard()
        {
            if (ClipboardManager.Contain() == ClipboardDataType.Text)
            {
                string clipboard = ClipboardManager.GetText();

                //save in temp to send
                FileInfo textFile = new FileInfo(Utils.SaveFileToTempPath(DateTime.Now.Ticks.ToString() + ".txt"));
                if (!File.Exists(textFile.FullName))
                {
                    using (var fs = File.Create(textFile.FullName))
                    {
                        fs.Close();
                    }
                }

                File.WriteAllText(textFile.FullName, clipboard);

                //todo: send file
            }
            else if (ClipboardManager.Contain() == ClipboardDataType.Image)
            {
                var clipboard = ClipboardManager.GetImage();
                var file = Screenshot.SaveBitmap(clipboard);

                //todo: send file
            }
        }

        private void Exit()
        {
            tray.Dispose();
            AppSettings.SaveSettingsFile();
            Utils.CheckRunAtStartup();
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
