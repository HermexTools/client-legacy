using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KSLUploader.Classes
{
    public class Logger
    {
        private static FileInfo logFile = new FileInfo(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData)+"\\.KSLU\\KSLULog.txt");
        
        public static void Info(string s)
        {
            Write(s, "[INFO]");
        }

        public static void Severe(string s)
        {
            Write(s, "[SEVERE]");
        }

        public static void Clear()
        {
            InitializeFile();
            File.WriteAllText(logFile.FullName, "");
        }

        private static void Write(string content, string type)
        {
            InitializeFile();
            File.AppendAllText(logFile.FullName,DateTime.Now.ToString("[dd/MM/yyyy H:mm:ss.FFF]")+" "+type+" "+content+"\n");            
        }

        private static void InitializeFile()
        {
            //create folder
            if(!Directory.Exists(logFile.DirectoryName))
            {
                Directory.CreateDirectory(logFile.DirectoryName);
            }

            //create file
            if(!File.Exists(logFile.FullName))
            {
                using(var fs = File.Create(logFile.FullName))
                {
                    fs.Close();
                }
            }
        }
    }
}
