using KSLUploader.Classes;
using System.Diagnostics;
using System.Linq;
using System.Windows;

namespace KSLUploader
{
    public partial class App : Application
    {
        private SystemTray ksuTray;

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
