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
        public static readonly string OwnAppDataFolderName = ".hermex";
        public static readonly string AppDataFolder = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + OwnAppDataFolderName + Path.DirectorySeparatorChar;
        public static readonly string TempFolder = Path.GetTempPath() + Path.DirectorySeparatorChar;
        public static readonly string SaveLocalFileNamePrefix = "hermex_";
        public static readonly string SaveTempFileNamePrefix = "hermex_temp_";
        public static readonly string Name = "Hermex";
        public static readonly string[] Developers = new string[] { "Kaos1337", "SergiX44", "Lukasss93" };
        public static readonly string Version = "0.0.3-Beta";
        public static readonly string SaveLocalDefaultPath = Environment.GetFolderPath(Environment.SpecialFolder.MyPictures);
        public static readonly string LoggerFileName = AppDataFolder + "logs.txt";
        public static readonly string SettingsFileName = AppDataFolder + "config.json";
        public static readonly string StartUpRegistryKey = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        public static readonly string StartUpRegistryKeyName = "Hermex";
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
    }
}
