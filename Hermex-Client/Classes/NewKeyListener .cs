using Gma.System.MouseKeyHook;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;
using System.Linq;
using System;
using System.Windows;
using System.Windows.Input;

namespace Hermex.Classes
{
    public class NewKeyListener
    {
        //library interface
        private IKeyboardMouseEvents keyHook;

        //supported keys with their name
        private static Dictionary<Keys, string> supportedKeys = new Dictionary<Keys, string>();

        //current combination when any key is pressed
        private HashSet<int> combination = new HashSet<int>();

        //integer values with the purpose of generating a correct combination 
        private int keydowncount = 0;
        private int keyupcount = 0;

        //item to check (if null use global)
        public UIElement element = null;

        public NewKeyListener()
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
            supportedKeys.Add(Keys.OemBackslash, "<");

            keyHook = Hook.GlobalEvents();
        }

        public delegate void KeyListenerEventHandler(object sender, KeyListenerEventArgs e);
        public event KeyListenerEventHandler OnKeyDown;
        public event KeyListenerEventHandler OnKeyUp;
        public event KeyListenerEventHandler OnCombinationCompleted;        

        public void EnableListener()
        {
            if(element == null)
            {
                keyHook.KeyDown += GlobalKeyDown;
                keyHook.KeyUp += GlobalKeyUp;
            }
            else
            {
                element.KeyDown += ElementKeyDown;
                element.KeyUp += ElementKeyUp;
            }
        }

        public void DisableListener()
        {
            if(element == null)
            {
                keyHook.KeyDown -= GlobalKeyDown;
                keyHook.KeyUp -= GlobalKeyUp;
            }
            else
            {
                element.KeyDown -= ElementKeyDown;
                element.KeyUp -= ElementKeyUp;
            }
        }

        private void GlobalKeyDown(object sender, System.Windows.Forms.KeyEventArgs e)
        {
            OnKeyDown?.Invoke(this, new KeyListenerEventArgs(combination, e.KeyValue));

            if(!combination.Contains(e.KeyValue) && combination.Count<=3)
            {
                keydowncount++;
                combination.Add(e.KeyValue);
            }
        }

        private void GlobalKeyUp(object sender, System.Windows.Forms.KeyEventArgs e)
        {
            OnKeyUp?.Invoke(this, new KeyListenerEventArgs(combination, e.KeyValue));

            if(combination.Contains(e.KeyValue))
            {
                keyupcount++;

                if(keydowncount == keyupcount)
                {
                    keydowncount = 0;
                    keyupcount = 0;
                    OnCombinationCompleted?.Invoke(this, new KeyListenerEventArgs(combination));
                    combination.Clear();
                }
            }
        }

        private void ElementKeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            OnKeyDown?.Invoke(this, new KeyListenerEventArgs(combination, (int)e.Key));

            if(!combination.Contains((int)e.Key) && combination.Count <= 3)
            {
                keydowncount++;
                combination.Add((int)e.Key);
            }
        }

        private void ElementKeyUp(object sender, System.Windows.Input.KeyEventArgs e)
        {
            OnKeyUp?.Invoke(this, new KeyListenerEventArgs(combination, (int)e.Key));

            if(combination.Contains((int)e.Key))
            {
                keyupcount++;

                if(keydowncount == keyupcount)
                {
                    keydowncount = 0;
                    keyupcount = 0;
                    OnCombinationCompleted?.Invoke(this, new KeyListenerEventArgs(combination));
                    combination.Clear();
                }
            }
        }














        public static string GetStringCombination(HashSet<int> keyValues)
        {
            StringBuilder output = new StringBuilder();
            foreach(var item in keyValues)
            {
                output.Append(supportedKeys.First(x => x.Key == (Keys)item).Value);
                output.Append("+");
            }
            output.Remove(output.ToString().LastIndexOf("+"), 1);
            return output.ToString();
        }
    }
    

    public class KeyListenerEventArgs : EventArgs
    {
        public HashSet<int> Combination { get; set; }
        public int EventKey { get; set; }

        public KeyListenerEventArgs() { }

        public KeyListenerEventArgs(HashSet<int> combination)
        {
            Combination = combination;
        }

        public KeyListenerEventArgs(HashSet<int> combination, int eventkey)
        {
            Combination = combination;
            EventKey = eventkey;
        }
    }
}
