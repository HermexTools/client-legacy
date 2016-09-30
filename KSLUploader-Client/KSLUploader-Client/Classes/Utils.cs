using Microsoft.Win32;
using System.Linq;
using System.Windows;
using System.Collections.Generic;

namespace KSLUploader.Classes
{
    public class Utils
    {
        public static bool IsWindowOpen<T>(string name = "") where T : System.Windows.Window
        {
            return string.IsNullOrEmpty(name)
               ? App.Current.Windows.OfType<T>().Any()
               : App.Current.Windows.OfType<T>().Any(w => w.Name.Equals(name));
        }

        public static void CheckRunAtStartup()
        {
            var registryKey = AppConstants.StartUpRegistryKey;

            if (SettingsManager.Get<bool>("RunAtStartup"))
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey(registryKey, true))
                {
                    key.SetValue(AppConstants.StartUpRegistryKeyName, "\"" + System.Reflection.Assembly.GetExecutingAssembly().Location + "\"");
                }
            }
            else
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey(registryKey, true))
                {
                    key.DeleteValue(AppConstants.StartUpRegistryKeyName, false);
                }
            }
        }
        
        public static string CoolMs(string ms)
        {
            if(ms.Length==1)
            {
                return "00" + ms;
            }
            else if(ms.Length == 2)
            {
                return "0" + ms;
            }
            else
            {
                return ms;
            }
        }
    }
}
