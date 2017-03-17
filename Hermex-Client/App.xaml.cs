using Hermex.Classes;
using System.Diagnostics;
using System.Windows;

namespace Hermex
{
    public partial class App : Application
    {
        public SystemTray TrayIcon;
        public KeyListener GlobalKeyListener;

        protected override void OnStartup(StartupEventArgs e)
        {
            //no double instance
            if(Process.GetProcessesByName(Process.GetCurrentProcess().ProcessName).Length > 1)
            {
                MessageBox.Show(AppConstants.Name + " is already running.", "Error");
                Current.Shutdown();
                return;
            }

            //log app start
            Logger.Set(AppConstants.Name + " started!");

            //force explicit shutdown
            Current.ShutdownMode = ShutdownMode.OnExplicitShutdown;

            //initialize app settings at first app run
            AppSettings.InitializeSettings();

            //check registry key for run at startup
            Utils.CheckRunAtStartup();
            
            //enable global keylistener
            GlobalKeyListener = new KeyListener();
            GlobalKeyListener.EnableListener();

            //start trayicon
            TrayIcon = new SystemTray();
        }
        
    }
}
