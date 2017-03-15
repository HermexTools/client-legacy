using Microsoft.Win32;
using System.Linq;
using System.Windows;
using System.Collections.Generic;
using System;
using System.Text.RegularExpressions;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace Hermex.Classes
{
    public class Utils
    {
        //check if app is already running
        public static bool IsWindowOpen<T>(string name = "") where T : Window
        {
            return string.IsNullOrEmpty(name)
               ? App.Current.Windows.OfType<T>().Any()
               : App.Current.Windows.OfType<T>().Any(w => w.Name.Equals(name));
        }

        //check registry key for run at startup
        public static void CheckRunAtStartup()
        {
            var registryKey = AppConstants.StartUpRegistryKey;

            if (AppSettings.Get<bool>("RunAtStartup"))
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

        //generate a file name
        public static string GenerateName()
        {
            string filename = DateTime.Now.ToString("ddMMyy-HHmmss");
            return new Random().Next(999) + "-" + filename;
        }

        //generate a file from string
        public static string GenerateName(string name)
        {
            string st = name;
            st = Regex.Replace(st, "[-+^,èòàù()%&:\\[\\]{\\}]", "");
            st = st.Replace(" ", "_");
            return new Random().Next(99999) + "-" + st;
        }

        //save a image from path to a local folder
        public static string SaveImageToLocalPath(string filename)
        {
            return AppSettings.Get<string>("SaveLocalPath") + Path.DirectorySeparatorChar + AppConstants.SaveLocalFileNamePrefix + filename;
        }

        //save a file from path to temp folder
        public static string SaveFileToTempPath(string filename)
        {
            return AppConstants.TempFolder + AppConstants.SaveTempFileNamePrefix + filename;
        }

        //get a human string key combination
        public static string GetStringCombination(HashSet<int> keyValues)
        {
            StringBuilder output = new StringBuilder();
            foreach(var item in keyValues)
            {
                output.Append(AppConstants.SupportedKeys[(Keys)item]);
                output.Append("+");
            }
            output.Remove(output.ToString().LastIndexOf("+"), 1);
            return output.ToString();
        }

        //set a custom tooltip
        public static void SetTooltip(NotifyIcon icon, int timeout, string title, string text, ToolTipIcon tipicon)
        {
            icon.BalloonTipTitle = title;
            icon.BalloonTipText = text;
            icon.BalloonTipIcon = tipicon;

            icon.ShowBalloonTip(timeout);
        }

        //check if a string is a valid url
        public static bool CheckURL(string source)
        {
            Uri uriResult;
            return Uri.TryCreate(source, UriKind.Absolute, out uriResult) && uriResult.Scheme == Uri.UriSchemeHttp;
        }
    }
}
