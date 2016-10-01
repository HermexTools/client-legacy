using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KSLUploader.Classes
{
    public class AppConstants
    {
        //private fields
        private static readonly string OwnAppDataFolderName = ".KSLU";
        private static readonly string AppDataFolder = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + OwnAppDataFolderName + Path.DirectorySeparatorChar;
        private static readonly string TempFolder = Path.GetTempPath() + Path.DirectorySeparatorChar;
        private static readonly string SaveLocalFileNamePrefix = "kslu_";
        private static readonly string SaveTempFileNamePrefix = "kslu_temp_";

        //public fields
        public static readonly string Name = "KSLUploader";
        public static readonly string[] Developers = new string[] { "Kaos1337", "SergiX44", "Lukasss93" };
        public static readonly string Version = "0.0.3-Beta";
        public static readonly string SaveLocalDefaultPath = Environment.GetFolderPath(Environment.SpecialFolder.MyPictures);
        public static readonly string LoggerFileName = AppDataFolder + "logs.txt";
        public static readonly string SettingsFileName = AppDataFolder + "config.json";
        public static readonly string StartUpRegistryKey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        public static readonly string StartUpRegistryKeyName = "KSLU";

        public static string SaveImageToLocalPath(string filename)
        {
            return SettingsManager.Get<string>("SaveLocalPath") + Path.DirectorySeparatorChar + SaveLocalFileNamePrefix + filename;
        }

        public static string SaveFileToTempPath(string filename)
        {
            return TempFolder + SaveTempFileNamePrefix + filename;
        }

        public static void InitializeSettings()
        {
            //generals
            SettingsManager.Initialize("RunAtStartup", false);
            SettingsManager.Initialize("SaveLocal", false);
            SettingsManager.Initialize("SaveLocalPath", SaveLocalDefaultPath);
            SettingsManager.Initialize("UploadMethod", "SOCKET");

            //protocol
            SettingsManager.Initialize("SocketAddress", "localhost");
            SettingsManager.Initialize("SocketPort", 4030);
            SettingsManager.Initialize("SocketPassword", "pass");

            //ftp
            SettingsManager.Initialize("UseFTPS", false);
            SettingsManager.Initialize("AcceptCertificates", true);
            SettingsManager.Initialize("FTPAddress", null);
            SettingsManager.Initialize("FTPPort", 21);
            SettingsManager.Initialize("FTPDirectory", "/");
            SettingsManager.Initialize("FTPWeburl", null);
            SettingsManager.Initialize("FTPUser", null);
            SettingsManager.Initialize("FTPPassword", null);

            //shortcut
            SettingsManager.Initialize("ShortcutArea", new HashSet<int>() { 162, 160, 49 });
            SettingsManager.Initialize("ShortcutDesktop", new HashSet<int>() { 162, 160, 50 });
            SettingsManager.Initialize("ShortcutFile", new HashSet<int>() { 162, 160, 51 });
            SettingsManager.Initialize("ShortcutClipboard", new HashSet<int>() { 162, 160, 52 });
        }
    }
}
