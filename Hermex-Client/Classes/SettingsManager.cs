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
                file[key] = value;
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
            
            if(!Contains(key))
            {
                AppConstants.InitializeSettings();
            }

            dictionary = ReadSettingsFile();

            if(!(dictionary[key] is T))
            {
                Remove(key);
                AppConstants.InitializeSettings();
                dictionary = ReadSettingsFile();
            }

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


                AppConstants.InitializeSettings();
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

    }
}
