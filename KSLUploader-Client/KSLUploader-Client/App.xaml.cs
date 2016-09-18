using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
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
            base.OnStartup(e);

            st = new SystemTray();
        }

        public static void ExitApplication()
        {
            Current.Shutdown();
        }
    }
}
