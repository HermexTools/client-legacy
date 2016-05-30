using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
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
            smenu.Items.Add("Debug",null, DebugEvent);
            smenu.Items.Add("-");
            smenu.Items.Add("Settings", Properties.Resources.Settings, Settings);
            smenu.Items.Add("-");
            smenu.Items.Add("Quit",Properties.Resources.Quit, Quit);

            smenu.Items[0].Enabled = false;
            trayIcon.ContextMenuStrip = smenu;

            //tray icon double click event
            trayIcon.MouseDoubleClick += MouseClick;

            //show the tray icon
            trayIcon.Visible = true;
        }

        private void DebugEvent(object sender, EventArgs e)
        {
            StringBuilder mystring = new StringBuilder();
            mystring.AppendLine("use_ftp: "+Properties.Settings.Default.use_ftp.ToString());
            mystring.AppendLine("use_ftps: " + Properties.Settings.Default.use_ftps.ToString());            
            mystring.AppendLine("save_image: " + Properties.Settings.Default.save_image.ToString());
            mystring.AppendLine("open_startup: " + Properties.Settings.Default.open_startup.ToString());
            mystring.AppendLine();
            mystring.AppendLine("ftp_address: " + Properties.Settings.Default.ftp_address);
            mystring.AppendLine("ftp_port: " + Properties.Settings.Default.ftp_port);
            mystring.AppendLine("ftp_directory: " + Properties.Settings.Default.ftp_directory);
            mystring.AppendLine("ftp_weburl: " + Properties.Settings.Default.ftp_weburl);
            mystring.AppendLine("ftp_user: " + Properties.Settings.Default.ftp_user);
            mystring.AppendLine("ftp_password: " + Properties.Settings.Default.ftp_password);
            mystring.AppendLine("ftp_certificate: " + Properties.Settings.Default.ftp_certificate.ToString());
            mystring.AppendLine();
            mystring.AppendLine("server_address: " + Properties.Settings.Default.server_address);
            mystring.AppendLine("server_password: " + Properties.Settings.Default.server_password);
            mystring.AppendLine("server_port: " + Properties.Settings.Default.server_port);
            mystring.AppendLine();
            mystring.AppendLine("shortcut_area: " + Properties.Settings.Default.shortcut_area);
            mystring.AppendLine("shortcut_desktop: " + Properties.Settings.Default.shortcut_desktop);
            mystring.AppendLine("shortcut_file: " + Properties.Settings.Default.shortcut_file);
            mystring.AppendLine("shortcut_clipboard: " + Properties.Settings.Default.shortcut_clipboard);


            MessageBox.Show(mystring.ToString(), "SETTINGS DEBUG");
        }

        private void MouseClick(object sender, MouseEventArgs e)
        {
            MessageBox.Show("Double click! Woooooo");            
        }
        
        private void Settings(object sender, EventArgs e)
        {
            if(!CheckFormIsOpened("Settings"))
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

        private bool CheckFormIsOpened(string name)
        {
            FormCollection fc = Application.OpenForms;

            foreach(Form frm in fc)
            {
                if(frm.Text == name)
                {
                    frm.Focus();
                    return true;
                }
            }
            return false;
        }
    }
}
