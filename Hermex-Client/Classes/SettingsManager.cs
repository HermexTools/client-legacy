using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;

namespace Hermex.Classes
{
    public class SettingsManager
    {
        private static FileInfo SettingsFile = new FileInfo(AppConstants.SettingsFileName);

        public static void Initialize(string key, object value)
        {
            if (!Contains(key))
            {
                Set(key, value);
            }
        }

        public static void Set(string key, object value)
        {
            var file = ReadSettingsFile();

            if (Contains(key))
            {
                if(file[key] is HashSet<int> && value is HashSet<int>)
                {
                    ((HashSet<int>)file[key]).Clear();
                    ((HashSet<int>)file[key]).UnionWith((HashSet<int>)value);
                }
                else
                {
                    file[key] = value;
                }
            }
            else
            {
                file.Add(key, value);
            }

            SaveSettingsFile(file);
        }

        public static T Get<T>(string key)
        {
            Dictionary<string, object> dictionary = null;
            
            dictionary = ReadSettingsFile();

            if (dictionary[key] is JArray)
            {
                return ((JArray)dictionary[key]).ToObject<T>();
            }

            return (T)Convert.ChangeType(dictionary[key], typeof(T));
        }

        public static void Remove(string key)
        {
            var file = ReadSettingsFile();
            file.Remove(key);
            SaveSettingsFile(file);
        }

        public static void RemoveAll()
        {
            var file = ReadSettingsFile();
            foreach (var item in file)
            {
                Remove(item.Key);
            }
        }

        public static bool Contains(string key)
        {
            var file = ReadSettingsFile();
            return file.ContainsKey(key);
        }



        public static Dictionary<string, object> ReadSettingsFile()
        {
            bool error = false;
            Dictionary<string, object> list = new Dictionary<string, object>();

            try
            {
                var file = GetSettingFile();
                string content = File.ReadAllText(file.FullName);
                list = JsonConvert.DeserializeObject<Dictionary<string, object>>(content);
            }
            catch
            {
                error = true;
            }

            if (error)
            {
                var file = GetSettingFile();
                File.WriteAllText(file.FullName, JsonConvert.SerializeObject(new Dictionary<string, object>()));


                InitializeSettings();
                string content = File.ReadAllText(file.FullName);
                list = JsonConvert.DeserializeObject<Dictionary<string, object>>(content);                
            }

            return list;
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

        private static void SaveSettingsFile(Dictionary<string, object> settings)
        {
            var file = GetSettingFile();
            File.WriteAllText(file.FullName, JsonConvert.SerializeObject(settings, Formatting.Indented));
        }

        public static void InitializeSettings()
        {
            //generals
            Initialize("RunAtStartup", false);
            Initialize("SaveLocal", false);
            Initialize("SaveLocalPath", AppConstants.SaveLocalDefaultPath);
            Initialize("UploadMethod", "SOCKET");

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
        }

    }
}
