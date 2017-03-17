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

        //current combination when any key is pressed
        private HashSet<int> combination = new HashSet<int>();

        //integer values with the purpose of generating a correct combination 
        private int keydowncount = 0;
        private int keyupcount = 0;

        public NewKeyListener()
        {
            keyHook = Hook.GlobalEvents();
        }

        public delegate void KeyListenerEventHandler(object sender, KeyListenerEventArgs e);
        public event KeyListenerEventHandler OnKeyPressed;
        public event KeyListenerEventHandler OnCombinationCompleted;

        public void EnableListener()
        {
            keyHook.KeyDown += GlobalKeyDown;
            keyHook.KeyUp += GlobalKeyUp;
        }

        public void DisableListener()
        {
            keyHook.KeyDown -= GlobalKeyDown;
            keyHook.KeyUp -= GlobalKeyUp;
        }

        private void GlobalKeyDown(object sender, System.Windows.Forms.KeyEventArgs e)
        {
            if(!combination.Contains(e.KeyValue) && combination.Count < 3)
            {
                keydowncount++;
                combination.Add(e.KeyValue);
                OnKeyPressed?.Invoke(this, new KeyListenerEventArgs(combination));
            }
        }

        private void GlobalKeyUp(object sender, System.Windows.Forms.KeyEventArgs e)
        {
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
    }


    public class KeyListenerEventArgs : EventArgs
    {
        public HashSet<int> Combination { get; set; }

        public KeyListenerEventArgs() { }

        public KeyListenerEventArgs(HashSet<int> combination)
        {
            Combination = combination;
        }
    }
}
