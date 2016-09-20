using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;

namespace KSLUploader
{
    /// <summary>
    /// Logica di interazione per App.xaml
    /// </summary>
    public partial class App : Application
    {
        private SystemTray st;

        protected override void OnStartup(StartupEventArgs e)
        {
            //no double instance!
            if(Process.GetProcessesByName(Process.GetCurrentProcess().ProcessName).Length > 1)
            {
                MessageBox.Show("KSLUploader is already running.", "Error");
                Current.Shutdown();
            }

            base.OnStartup(e);

            st = new SystemTray();
        }

        public static void ExitApplication()
        {
            Current.Shutdown();
        }
    }
}
