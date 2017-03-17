using System;
using System.Diagnostics;
using System.IO;
using System.Text;

namespace Hermex.Classes
{
    public enum LogType
    {
        INFO,
        SEVERE
    }

    public class Logger
    {
        private static FileInfo logFile = new FileInfo(AppConstants.LoggerFileName);

        public static void Set(string message, LogType type = LogType.INFO)
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

            var actualTime = DateTime.Now;
            var day = actualTime.Day.ToString().PadLeft(2, '0');
            var month = actualTime.Month.ToString().PadLeft(2, '0');
            var year = actualTime.Year;
            var hour = actualTime.Hour.ToString().PadLeft(2, '0');
            var minute = actualTime.Minute.ToString().PadLeft(2, '0');
            var second = actualTime.Second.ToString().PadLeft(2, '0');
            var millisecond = actualTime.Millisecond.ToString().PadLeft(3, '0');

            StringBuilder row = new StringBuilder();
            row.AppendFormat("[{0}/{1}/{2} {3}:{4}:{5}.{6}] ", day, month, year, hour, minute, second, millisecond);
            row.AppendFormat("[{0}|{1}] ", type, className);
            row.AppendLine(content);

            File.AppendAllText(logFile.FullName, row.ToString());
        }

        private static void InitializeFile()
        {
            //create folder
            if (!Directory.Exists(logFile.DirectoryName))
            {
                Directory.CreateDirectory(logFile.DirectoryName);
            }

            //create file
            if (!File.Exists(logFile.FullName))
            {
                using (var fs = File.Create(logFile.FullName))
                {
                    fs.Close();
                }
            }
        }
    }
}
