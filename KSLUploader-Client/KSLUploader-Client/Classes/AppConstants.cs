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

        public static void InizializeSettings()
        {
            //generals
            SettingsManager.Inizialize("RunAtStartup", false);
            SettingsManager.Inizialize("SaveLocal", false);
            SettingsManager.Inizialize("SaveLocalPath", SaveLocalDefaultPath);
            SettingsManager.Inizialize("UploadMethod", "SOCKET");

            //protocol
            SettingsManager.Inizialize("SocketAddress", "localhost");
            SettingsManager.Inizialize("SocketPort", 4030);
            SettingsManager.Inizialize("SocketPassword", "pass");

            //ftp
            SettingsManager.Inizialize("UseFTPS", false);
            SettingsManager.Inizialize("AcceptCertificates", true);
            SettingsManager.Inizialize("FTPAddress", null);
            SettingsManager.Inizialize("FTPPort", 21);
            SettingsManager.Inizialize("FTPDirectory", "/");
            SettingsManager.Inizialize("FTPWeburl", null);
            SettingsManager.Inizialize("FTPUser", null);
            SettingsManager.Inizialize("FTPPassword", null);

            //shortcut
            SettingsManager.Inizialize("ShortcutArea", new HashSet<int>() { 162, 160, 49 });
            SettingsManager.Inizialize("ShortcutDesktop", new HashSet<int>() { 162, 160, 50 });
            SettingsManager.Inizialize("ShortcutFile", new HashSet<int>() { 162, 160, 51 });
            SettingsManager.Inizialize("ShortcutClipboard", new HashSet<int>() { 162, 160, 52 });
        }
    }
}
