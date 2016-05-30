using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
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
