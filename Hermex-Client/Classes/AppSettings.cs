using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;

namespace Hermex.Classes
{
    public class AppSettings
    {
        private static FileInfo SettingsFile = new FileInfo(AppConstants.SettingsFileName);
        private static Dictionary<string, object> CurrentSettings;

        public static void Initialize(string key, object value)
        {
            if(!Contains(key))
            {
                Set(key, value);
            }
        }

        public static void Set(string key, object value)
        {
            if(Contains(key))
            {
                CurrentSettings[key] = value;
            }
            else
            {
                CurrentSettings.Add(key, value);
            }
        }

        public static T Get<T>(string key)
        {
            if(CurrentSettings[key] is JArray)
            {
                return ((JArray)CurrentSettings[key]).ToObject<T>();
            }

            return (T)Convert.ChangeType(CurrentSettings[key], typeof(T));
        }

        public static void Remove(string key)
        {
            CurrentSettings.Remove(key);
        }

        public static void RemoveAll()
        {
            foreach(var item in CurrentSettings)
            {
                Remove(item.Key);
            }
        }

        public static bool Contains(string key)
        {
            return CurrentSettings.ContainsKey(key);
        }



        public static void ReadSettingsFile()
        {
            bool error = false;

            try
            {
                var file = GetSettingFile();
                string content = File.ReadAllText(file.FullName);
                CurrentSettings = JsonConvert.DeserializeObject<Dictionary<string, object>>(content);
            }
            catch
            {
                error = true;
            }

            if(error)
            {
                var file = GetSettingFile();
                File.WriteAllText(file.FullName, JsonConvert.SerializeObject(new Dictionary<string, object>()));

                InitializeSettings();
                string content = File.ReadAllText(file.FullName);
                CurrentSettings = JsonConvert.DeserializeObject<Dictionary<string, object>>(content);
            }
        }

        private static FileInfo GetSettingFile()
        {
            //create folder
            if(!Directory.Exists(SettingsFile.DirectoryName))
            {
                Directory.CreateDirectory(SettingsFile.DirectoryName);
            }

            //create file
            if(!File.Exists(SettingsFile.FullName))
            {
                using(var fs = File.Create(SettingsFile.FullName))
                {
                    fs.Close();
                }
                File.WriteAllText(SettingsFile.FullName, JsonConvert.SerializeObject(new Dictionary<string, object>(), Formatting.Indented));
            }

            return SettingsFile;
        }

        public static void SaveSettingsFile()
        {
            var file = GetSettingFile();
            File.WriteAllText(file.FullName, JsonConvert.SerializeObject(CurrentSettings, Formatting.Indented));
        }

        public static void InitializeSettings()
        {
            ReadSettingsFile();

            //generals
            Initialize("RunAtStartup", false);
            Initialize("SaveLocal", false);
            Initialize("SaveLocalPath", AppConstants.SaveLocalDefaultPath);
            Initialize("UploadMethod", "SOCKET");
            Initialize("RecentItems", new Queue<string>());

            //protocol
            Initialize("SocketAddress", "localhost");
            Initialize("SocketPort", 4030);
            Initialize("SocketPassword", "pass");

            //ftp
            Initialize("UseFTPS", false);
            Initialize("AcceptCertificates", true);
            Initialize("FTPAddress", null);
            Initialize("FTPPort", 21);
            Initialize("FTPDirectory", "/");
            Initialize("FTPWeburl", null);
            Initialize("FTPUser", null);
            Initialize("FTPPassword", null);

            //shortcut
            Initialize("ShortcutArea", new HashSet<int>() { 162, 160, 49 });
            Initialize("ShortcutDesktop", new HashSet<int>() { 162, 160, 50 });
            Initialize("ShortcutFile", new HashSet<int>() { 162, 160, 51 });
            Initialize("ShortcutClipboard", new HashSet<int>() { 162, 160, 52 });

            SaveSettingsFile();
        }

    }
}
