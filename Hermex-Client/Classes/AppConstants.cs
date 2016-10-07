using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Hermex.Classes
{
    public class AppConstants
    {
        //private fields
        private static readonly string OwnAppDataFolderName = ".hermex";
        private static readonly string AppDataFolder = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + OwnAppDataFolderName + Path.DirectorySeparatorChar;
        private static readonly string TempFolder = Path.GetTempPath() + Path.DirectorySeparatorChar;
        private static readonly string SaveLocalFileNamePrefix = "hermex_";
        private static readonly string SaveTempFileNamePrefix = "hermex_temp_";

        //public fields
        public static readonly string Name = "Hermex";
        public static readonly string[] Developers = new string[] { "Kaos1337", "SergiX44", "Lukasss93" };
        public static readonly string Version = "0.0.3-Beta";
        public static readonly string SaveLocalDefaultPath = Environment.GetFolderPath(Environment.SpecialFolder.MyPictures);
        public static readonly string LoggerFileName = AppDataFolder + "logs.txt";
        public static readonly string SettingsFileName = AppDataFolder + "config.json";
        public static readonly string StartUpRegistryKey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        public static readonly string StartUpRegistryKeyName = "Hermex";

        public static string SaveImageToLocalPath(string filename)
        {
            return SettingsManager.Get<string>("SaveLocalPath") + Path.DirectorySeparatorChar + SaveLocalFileNamePrefix + filename;
        }

        public static string SaveFileToTempPath(string filename)
        {
            return TempFolder + SaveTempFileNamePrefix + filename;
        }


        public static readonly Dictionary<Keys, string> SupportedKeys = new Dictionary<Keys, string>()
        {
            { Keys.LControlKey, "CTRL-L"},
            { Keys.RControlKey, "CTRL-R"},
            { Keys.LShiftKey, "SHIFT-L"},
            { Keys.RShiftKey, "SHIFT-R"},
            { Keys.LMenu, "ALT-L"},
            { Keys.RMenu, "ALT-R"},
            { Keys.D0, "0"},
            { Keys.D1, "1"},
            { Keys.D2, "2"},
            { Keys.D3, "3"},
            { Keys.D4, "4"},
            { Keys.D5, "5"},
            { Keys.D6, "6"},
            { Keys.D7, "7"},
            { Keys.D8, "8"},
            { Keys.D9, "9"},
            { Keys.NumPad0, "NumPad 0"},
            { Keys.NumPad1, "NumPad 1"},
            { Keys.NumPad2, "NumPad 2"},
            { Keys.NumPad3, "NumPad 3"},
            { Keys.NumPad4, "NumPad 4"},
            { Keys.NumPad5, "NumPad 5"},
            { Keys.NumPad6, "NumPad 6"},
            { Keys.NumPad7, "NumPad 7"},
            { Keys.NumPad8, "NumPad 8"},
            { Keys.NumPad9, "NumPad 9"},
            { Keys.A, "A"},
            { Keys.B, "B"},
            { Keys.C, "C"},
            { Keys.D, "D"},
            { Keys.E, "E"},
            { Keys.F, "F"},
            { Keys.G, "G"},
            { Keys.H, "H"},
            { Keys.I, "I"},
            { Keys.J, "J"},
            { Keys.K, "K"},
            { Keys.L, "L"},
            { Keys.M, "M"},
            { Keys.N, "N"},
            { Keys.O, "O"},
            { Keys.P, "P"},
            { Keys.Q, "Q"},
            { Keys.R, "R"},
            { Keys.S, "S"},
            { Keys.T, "T"},
            { Keys.U, "U"},
            { Keys.V, "V"},
            { Keys.W, "W"},
            { Keys.X, "X"},
            { Keys.Y, "Y"},
            { Keys.Z, "Z"},
            { Keys.Space, "SPACE"},
            { Keys.OemBackslash, "<"}
        };

        public static string GetStringCombination(HashSet<int> keyValues)
        {
            StringBuilder output = new StringBuilder();
            foreach(var item in keyValues)
            {
                output.Append(SupportedKeys[(Keys)item]);
                output.Append("+");
            }
            output.Remove(output.ToString().LastIndexOf("+"), 1);
            return output.ToString();
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
            //todo: setting will be initialized every time... why? boh...
        }
    }
}
