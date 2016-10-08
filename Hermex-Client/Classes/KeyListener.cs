using Gma.System.MouseKeyHook;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;
using System.Linq;
using System;

namespace Hermex.Classes
{
    public class KeyListener
    {
        //library interface
        private IKeyboardMouseEvents keyHook;
        
        //current combination when any key is pressed
        private HashSet<int> combination = new HashSet<int>();

        //integer values with the purpose of generating a correct combination 
        private int keydowncount = 0;
        private int keyupcount = 0;

        //mode: true=read; false=set;
        private bool IsReadMode = true;

        public KeyListener()
        {
            keyHook = Hook.GlobalEvents();
        }

        public delegate void ShortcutEventHandler(object sender, ShortcutEventArgs e);
        public event ShortcutEventHandler OnShortcutEvent;

        private void HookKeyDown(object sender, KeyEventArgs e)
        {
            if(!combination.Contains(e.KeyValue))
            {
                keydowncount++;
                combination.Add(e.KeyValue);
            }
        }

        private void HookKeyUp(object sender, KeyEventArgs e)
        {
            if(combination.Contains(e.KeyValue))
            {
                keyupcount++;

                if(keydowncount == keyupcount)
                {
                    keydowncount = 0;
                    keyupcount = 0;
                    RunCombination();
                    combination.Clear();
                }
            }
        }

        private void RunCombination()
        {
            if(IsReadMode)
            {
                if(combination.SetEquals(AppSettings.Get<HashSet<int>>("ShortcutArea")))
                {
                    OnShortcutEvent?.Invoke(this, new ShortcutEventArgs(ShortcutEvent.ShortcutArea));
                }
                else if(combination.SetEquals(AppSettings.Get<HashSet<int>>("ShortcutDesktop")))
                {
                    OnShortcutEvent?.Invoke(this, new ShortcutEventArgs(ShortcutEvent.ShortcutDesktop));
                }
                else if(combination.SetEquals(AppSettings.Get<HashSet<int>>("ShortcutFile")))
                {
                    OnShortcutEvent?.Invoke(this, new ShortcutEventArgs(ShortcutEvent.ShortcutFile));
                }
                else if(combination.SetEquals(AppSettings.Get<HashSet<int>>("ShortcutClipboard")))
                {
                    OnShortcutEvent?.Invoke(this, new ShortcutEventArgs(ShortcutEvent.ShortcutClipboard));
                }
            }
            else
            {
                //todo: SetCombination
            }
        }
        
        public void EnableListener()
        {
            keyHook.KeyDown += HookKeyDown;
            keyHook.KeyUp += HookKeyUp;
        }

        public void DisableListener()
        {
            keyHook.KeyDown -= HookKeyDown;
            keyHook.KeyUp -= HookKeyUp;
        }
    }

    public enum ShortcutEvent
    {
        ShortcutArea,
        ShortcutDesktop,
        ShortcutFile,
        ShortcutClipboard
    }

    public class ShortcutEventArgs : EventArgs
    {
        public ShortcutEvent shortcutEvent { get; private set; }
        public ShortcutEventArgs(ShortcutEvent e)
        {
            shortcutEvent = e;
        }
    }
}
