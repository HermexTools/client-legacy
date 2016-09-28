 using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;
using System.Dynamic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Microsoft.Win32;

namespace KSLUploader.Classes
{
    public class Utils
    {
        public static Bitmap GetBitmapFromScreen(Screen screen)
        {
            Bitmap bmp = new Bitmap(screen.Bounds.Width, screen.Bounds.Height);
            using(Graphics g = Graphics.FromImage(bmp))
            {
                g.CopyFromScreen(screen.Bounds.X, screen.Bounds.Y, 0, 0, screen.Bounds.Size);
            }
            return bmp;
        }

        public static Bitmap GetBitmapFromPoints(Point start, Point end)
        {
            Bitmap bmp = new Bitmap(end.X-start.X, end.Y-start.Y);
            using(Graphics g = Graphics.FromImage(bmp))
            {
                g.CopyFromScreen(start.X, start.Y, 0, 0, new Size(end.X - start.X, end.Y - start.Y));
            }

            return bmp;
        }

        public static Bitmap JoinBitmaps(List<Bitmap> bitmaps)
        {
            Bitmap output = null;

            for(int i = 0; i < bitmaps.Count; i++)
            {
                if(i == 0)
                {
                    output = bitmaps[i];
                }
                else
                {
                    output = Join2Bitmap(output, bitmaps[i]);
                }
            }

            return output;
        }

        public static Bitmap Join2Bitmap(Bitmap first, Bitmap second)
        {
            int outputImageWidth = first.Width + second.Width;
            int outputImageHeight = first.Height > second.Height ? first.Height : second.Height;

            Bitmap outputImage = new Bitmap(outputImageWidth, outputImageHeight, System.Drawing.Imaging.PixelFormat.Format32bppArgb);

            using(Graphics graphics = Graphics.FromImage(outputImage))
            {
                graphics.DrawImage(first, new Rectangle(new Point(), first.Size), new Rectangle(new Point(), first.Size), GraphicsUnit.Pixel);
                graphics.DrawImage(second, new Rectangle(new Point(first.Width, 0), second.Size), new Rectangle(new Point(), second.Size), GraphicsUnit.Pixel);
            }

            return outputImage;
        }

        public static FileInfo SaveBitmap(Bitmap bitmap)
        {
            //save local if enabled
            if(SettingsManager.Get<bool>("SaveLocal"))
            {
                bitmap.Save(AppConstants.SaveLocalFileName, ImageFormat.Png);
            }

            //save in temp to send
            FileInfo f = new FileInfo(AppConstants.SaveTempFileName);
            bitmap.Save(f.FullName, ImageFormat.Png);
            return f;
        }  
        
        public static Point FromWindowsToDrawingPoint(System.Windows.Point start)
        {
            return new Point(Convert.ToInt32(start.X), Convert.ToInt32(start.Y));
        }

        public static bool IsWindowOpen<T>(string name = "") where T : System.Windows.Window
        {
            return string.IsNullOrEmpty(name)
               ? App.Current.Windows.OfType<T>().Any()
               : App.Current.Windows.OfType<T>().Any(w => w.Name.Equals(name));
        }

        public static void CheckRunAtStartup()
        {
            var registryKey = AppConstants.StartUpRegistryKey;

            if(SettingsManager.Get<bool>("RunAtStartup"))
            {
                using(RegistryKey key = Registry.CurrentUser.OpenSubKey(registryKey, true))
                {
                    key.SetValue(AppConstants.StartUpRegistryKeyName, "\"" + System.Reflection.Assembly.GetExecutingAssembly().Location + "\"");
                }
            }
            else
            {
                using(RegistryKey key = Registry.CurrentUser.OpenSubKey(registryKey, true))
                {
                    key.DeleteValue(AppConstants.StartUpRegistryKeyName, false);
                }
            }
        }
    }
}
