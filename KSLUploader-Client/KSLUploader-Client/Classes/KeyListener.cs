using Gma.System.MouseKeyHook;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;

namespace KSLUploader.Classes
{
    public class KeyListener
    {
        private IKeyboardMouseEvents keyHook;

        public KeyListener()
        {
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
                    FromKeyToName((Keys)item)
                    );
                output.Append("+");
            }
            output.Remove(output.ToString().LastIndexOf("+"), 1);
            return output.ToString();
        }
    }
}
