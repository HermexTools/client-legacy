using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Windows.Forms;
using KSLUploader.Windows;

namespace KSLUploader.Classes
{
    public class Screenshot
    {
        public static FileInfo CaptureDesktop()
        {
            List<Bitmap> bitmaps = new List<Bitmap>();

            foreach(var screen in Screen.AllScreens.OrderBy(x => x.Bounds.X).ToList())
            {
                bitmaps.Add(Utils.GetBitmapFromScreen(screen));
            }

            Bitmap image = Utils.JoinBitmaps(bitmaps);
            return Utils.SaveBitmap(image);
        }

        public static FileInfo CaptureDesktop(Screen screen)
        {
            return Utils.SaveBitmap(Utils.GetBitmapFromScreen(screen));
        }

        public static FileInfo CaptureArea()
        {
            var bmp = CaptureWindow.Capture();
            if(bmp != null)
            {
                return Utils.SaveBitmap(bmp);
            }
            return null;
        }
        
        public static FileInfo UploadClipboard()
        {
            if(Clipboard.ContainsText(TextDataFormat.Text))
            {
                string clipboard = Clipboard.GetText(TextDataFormat.Text);

                //save in temp to send
                FileInfo textFile = new FileInfo(Path.Combine(Path.GetTempPath(), "kslu_temp_" + DateTime.Now.Ticks + ".txt"));
                
                if(!File.Exists(textFile.FullName))
                {
                    using(var fs = File.Create(textFile.FullName))
                    {
                        fs.Close();
                    }
                }

                File.WriteAllText(textFile.FullName, clipboard);

                return textFile;
            }
            return null;
        }  
    }
}
