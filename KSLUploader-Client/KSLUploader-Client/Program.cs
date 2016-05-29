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
        //punto di ingresso principale dell'applicazione
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

            //inizializzazione della tray icon
            trayIcon = new NotifyIcon();

            //nome dell'icona
            trayIcon.Text = "KSLU";

            //icona
            trayIcon.Icon = Icon.FromHandle(Properties.Resources.AppIcon.GetHicon());

            //menu contestuale dell'icona
            ContextMenuStrip smenu = new ContextMenuStrip();
            smenu.Items.Add("KSLU v0.0.1 Beta", null, null);
            smenu.Items.Add("-");
            smenu.Items.Add("Settings", Properties.Resources.Settings, Settings);
            smenu.Items.Add("-");
            smenu.Items.Add("Quit",Properties.Resources.Quit, Quit);

            smenu.Items[0].Enabled = false;
            trayIcon.ContextMenuStrip = smenu;

            //faccio cose se clicco 2 volte sull'icona
            trayIcon.MouseDoubleClick += MouseClick;

            //rendi visibile l'icona
            trayIcon.Visible = true;
        }
                
        private void MouseClick(object sender, MouseEventArgs e)
        {
            MessageBox.Show("Hai cliccato 2 volte sull'icona! Woooooo");            
        }
        
        private void Settings(object sender, EventArgs e)
        {
            if(!CheckFormIsOpened("Settings"))
            {
                //apro la finestra delle impostazioni
                new Settings().ShowDialog();
            }
        }

        private void Quit(object sender, EventArgs e)
        {
            //nascondo l'icona dal tray, altrimenti rimane lì fino a quando non ci si passa col mouse
            trayIcon.Visible = false;

            //chiudo l'applicazione
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
