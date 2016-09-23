using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Gma.System.MouseKeyHook;

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
            Debug.WriteLine(e.KeyCode + " | " + e.KeyData + " | " + e.KeyValue+" | "+e.Modifiers);
        }
    }
}
