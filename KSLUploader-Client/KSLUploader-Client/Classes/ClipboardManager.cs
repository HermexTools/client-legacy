using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace KSLUploader.Classes
{
    public class ClipboardManager
    {
        public static string GetText()
        {
            if(Clipboard.ContainsText(TextDataFormat.Text))
            {
                return Clipboard.GetText(TextDataFormat.Text);
            }
            return null;
        }

        public static Bitmap GetImage()
        {
            if(Clipboard.ContainsImage())
            {
                return Screenshot.BitmapFromSource(Clipboard.GetImage());
            }
            return null;
        }

        public static void SetText(string text)
        {
            Clipboard.SetText(text);
        }

        public static ClipboardDataType Contain()
        {
            if(Clipboard.ContainsAudio())
            {
                return ClipboardDataType.Audio;
            }
            else if(Clipboard.ContainsFileDropList())
            {
                return ClipboardDataType.FileDropList;
            }
            else if(Clipboard.ContainsImage())
            {
                return ClipboardDataType.Image;
            }
            else if(Clipboard.ContainsText())
            {
                return ClipboardDataType.Text;
            }
            else
            {
                return ClipboardDataType.Void;
            }
        }
    }

    public enum ClipboardDataType
    {
        Text,
        Image,
        Audio,
        FileDropList,
        Void
    }
}
