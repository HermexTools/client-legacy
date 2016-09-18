using System;
using System.Diagnostics;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using KSLUploader_Client.Tools;
using KSLUploader_Client.Windows;

namespace KSLUploader_Client
{
    public static class KSUploader
    {
        //application entry point
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new SystemTray());
        }
    }
}
