using Gma.System.MouseKeyHook;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;
using System.Linq;

namespace KSLUploader.Classes
{
    public class KeyListener
    {
        private IKeyboardMouseEvents keyHook;
        private static Dictionary<Keys, string> supportedKeys = new Dictionary<Keys, string>();
        private int combinationMinLimit = 2;
        private int combinationMaxLimit = 3;

        public KeyListener()
        {
            supportedKeys.Add(Keys.LControlKey, "CTRL-L");
            supportedKeys.Add(Keys.RControlKey, "CTRL-R");
            supportedKeys.Add(Keys.LShiftKey, "SHIFT-L");
            supportedKeys.Add(Keys.RShiftKey, "SHIFT-R");
            supportedKeys.Add(Keys.LMenu, "ALT-L");
            supportedKeys.Add(Keys.RMenu, "ALT-R");
            supportedKeys.Add(Keys.D0, "0");
            supportedKeys.Add(Keys.D1, "1");
            supportedKeys.Add(Keys.D2, "2");
            supportedKeys.Add(Keys.D3, "3");
            supportedKeys.Add(Keys.D4, "4");
            supportedKeys.Add(Keys.D5, "5");
            supportedKeys.Add(Keys.D6, "6");
            supportedKeys.Add(Keys.D7, "7");
            supportedKeys.Add(Keys.D8, "8");
            supportedKeys.Add(Keys.D9, "9");
            supportedKeys.Add(Keys.NumPad0, "NumPad 0");
            supportedKeys.Add(Keys.NumPad1, "NumPad 1");
            supportedKeys.Add(Keys.NumPad2, "NumPad 2");
            supportedKeys.Add(Keys.NumPad3, "NumPad 3");
            supportedKeys.Add(Keys.NumPad4, "NumPad 4");
            supportedKeys.Add(Keys.NumPad5, "NumPad 5");
            supportedKeys.Add(Keys.NumPad6, "NumPad 6");
            supportedKeys.Add(Keys.NumPad7, "NumPad 7");
            supportedKeys.Add(Keys.NumPad8, "NumPad 8");
            supportedKeys.Add(Keys.NumPad9, "NumPad 9");
            supportedKeys.Add(Keys.A, "A");
            supportedKeys.Add(Keys.B, "B");
            supportedKeys.Add(Keys.C, "C");
            supportedKeys.Add(Keys.D, "D");
            supportedKeys.Add(Keys.E, "E");
            supportedKeys.Add(Keys.F, "F");
            supportedKeys.Add(Keys.G, "G");
            supportedKeys.Add(Keys.H, "H");
            supportedKeys.Add(Keys.I, "I");
            supportedKeys.Add(Keys.J, "J");
            supportedKeys.Add(Keys.K, "K");
            supportedKeys.Add(Keys.L, "L");
            supportedKeys.Add(Keys.M, "M");
            supportedKeys.Add(Keys.N, "N");
            supportedKeys.Add(Keys.O, "O");
            supportedKeys.Add(Keys.P, "P");
            supportedKeys.Add(Keys.Q, "Q");
            supportedKeys.Add(Keys.R, "R");
            supportedKeys.Add(Keys.S, "S");
            supportedKeys.Add(Keys.T, "T");
            supportedKeys.Add(Keys.U, "U");
            supportedKeys.Add(Keys.V, "V");
            supportedKeys.Add(Keys.W, "W");
            supportedKeys.Add(Keys.X, "X");
            supportedKeys.Add(Keys.Y, "Y");
            supportedKeys.Add(Keys.Z, "Z");
            supportedKeys.Add(Keys.Space, "SPACE");
            supportedKeys.Add(Keys.OemBackslash,"<");



            keyHook = Hook.GlobalEvents();
            keyHook.KeyUp += GlobalKeyApp;
        }

        private void GlobalKeyApp(object sender, System.Windows.Forms.KeyEventArgs e)
        {
            Debug.WriteLine(e.KeyCode + " | " + e.KeyData + " | " + e.KeyValue + " | " + e.Modifiers);
        }

        private static string FromKeyToName(Keys keys)
        {
            switch (keys)
            {
                case Keys.LControlKey: return "CTRL-L";
                case Keys.RControlKey: return "CTRL-R";
                case Keys.LShiftKey: return "SHIFT-L";
                case Keys.RShiftKey: return "SHIFT-R";
                case Keys.LMenu: return "ALT-L";
                case Keys.RMenu: return "ALT-R";
                default: return keys.ToString();
            }
        }

        public static string GetStringCombination(List<int> keyValues)
        {
            StringBuilder output = new StringBuilder();
            foreach (var item in keyValues)
            {
                output.Append(
                    supportedKeys.First(x=>x.Key==(Keys)item).Value

                    //FromKeyToName((Keys)item)
                    );
                output.Append(" + ");
            }
            output.Remove(output.ToString().LastIndexOf(" + "),3);
            return output.ToString();
        }
    }
}
