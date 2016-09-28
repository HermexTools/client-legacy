using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace KSLUploader.Classes
{
    public class SettingsManager
    {
        private static FileInfo SettingsFile = new FileInfo(AppConstants.SettingsFileName);

        public static void Inizialize(string key, object value)
        {
            if(!Contains(key))
            {
                Set(key, value);
            }
        }

        public static void Set(string key, object value)
        {
            var file = ReadSettingsFile();

            if(Contains(key))
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
            var file = ReadSettingsFile();
            
            if(file[key] is JArray)
            {
                return ((JArray)file[key]).ToObject<T>();
            }

            return (T)Convert.ChangeType(file[key], typeof(T));
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
            foreach(var item in file)
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

            if(error)
            {
                var file = GetSettingFile();
                File.WriteAllText(file.FullName, JsonConvert.SerializeObject(new Dictionary<string, object>()));
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

        private static void SaveSettingsFile(Dictionary<string,object> settings)
        {
            var file = GetSettingFile();
            File.WriteAllText(file.FullName, JsonConvert.SerializeObject(settings, Formatting.Indented));
        }

    }
}
