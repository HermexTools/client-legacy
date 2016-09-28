using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace KSLUploader.Classes
{
    public enum LogType { INFO, SEVERE }

    public class Logger
    {
        private static FileInfo logFile = new FileInfo(AppConstants.LoggerFileName);
        
        public static void Set(string message, LogType type)
        {
            Write(message, type.ToString(), new StackTrace().GetFrame(1).GetMethod().ReflectedType.Name);
        }

        public static void Clear()
        {
            InitializeFile();
            File.WriteAllText(logFile.FullName, "");
        }

        private static void Write(string content, string type, string className)
        {
            InitializeFile();
            File.AppendAllText(logFile.FullName, DateTime.Now.ToString("[dd-MM-yyyy H:mm:ss.FFF]") + " [" + type + "|" + className + "] " + content + "\n");     
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
