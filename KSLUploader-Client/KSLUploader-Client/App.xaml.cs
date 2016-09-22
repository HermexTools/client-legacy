using KSLUploader.Classes;
using KSLUploader.Classes.Uploaders;
using KSLUploader.Windows;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Diagnostics;
using System.IO;
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
        private SystemTray ksuTray;
        private Settings ksuSettings;

        protected override void OnStartup(StartupEventArgs e)
        {
            //no double instance!
            if(Process.GetProcessesByName(Process.GetCurrentProcess().ProcessName).Length > 1)
            {
                MessageBox.Show("KSLUploader is already running.", "Error");
                Current.Shutdown();
                return;
            }
           
            base.OnStartup(e);
            Current.ShutdownMode = ShutdownMode.OnExplicitShutdown;

            ksuTray = new SystemTray();
        }
        
        public static bool IsWindowOpen<T>(string name = "") where T : Window
        {
            return string.IsNullOrEmpty(name)
               ? Current.Windows.OfType<T>().Any()
               : Current.Windows.OfType<T>().Any(w => w.Name.Equals(name));
        }
    }
}
