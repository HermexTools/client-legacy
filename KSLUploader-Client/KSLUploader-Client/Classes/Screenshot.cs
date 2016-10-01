using KSLUploader.Windows;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Windows.Forms;
using System.Windows.Media.Imaging;

namespace KSLUploader.Classes
{
    public class Screenshot
    {
        public static FileInfo CaptureDesktop()
        {
            var left = Screen.AllScreens.Min(screen => screen.Bounds.X);
            var top = Screen.AllScreens.Min(screen => screen.Bounds.Y);
            var right = Screen.AllScreens.Max(screen => screen.Bounds.X + screen.Bounds.Width);
            var bottom = Screen.AllScreens.Max(screen => screen.Bounds.Y + screen.Bounds.Height);

            return SaveBitmap(GetBitmapFromPoints(new Point(left,top),new Point(right,bottom)));
        }

        public static FileInfo CaptureDesktop(Screen screen)
        {
            return SaveBitmap(GetBitmapFromScreen(screen));
        }

        public static FileInfo CaptureArea(Point start, Point end)
        {
            var bmp = GetBitmapFromPoints(start, end);
            return SaveBitmap(bmp);
        }

        private static Bitmap GetBitmapFromScreen(Screen screen)
        {
            Bitmap bmp = new Bitmap(screen.Bounds.Width, screen.Bounds.Height);
            using (Graphics g = Graphics.FromImage(bmp))
            {
                g.CopyFromScreen(screen.Bounds.X, screen.Bounds.Y, 0, 0, screen.Bounds.Size);
            }
            return bmp;
        }

        public static Bitmap GetBitmapFromPoints(Point start, Point end)
        {
            Bitmap bmp = new Bitmap(end.X - start.X, end.Y - start.Y);
            using (Graphics g = Graphics.FromImage(bmp))
            {
                g.CopyFromScreen(start.X, start.Y, 0, 0, new Size(end.X - start.X, end.Y - start.Y));
            }

            return bmp;
        }

        public static FileInfo SaveBitmap(Bitmap bitmap)
        {
            //save local if enabled
            if (SettingsManager.Get<bool>("SaveLocal"))
            {
                bitmap.Save(AppConstants.SaveImageToLocalPath(DateTime.Now.Ticks.ToString() + ".png"), ImageFormat.Png);
            }

            //save in temp to send
            FileInfo f = new FileInfo(AppConstants.SaveFileToTempPath(DateTime.Now.Ticks.ToString() + ".png"));
            bitmap.Save(f.FullName, ImageFormat.Png);
            return f;
        }

        public static Point FromWindowsToDrawingPoint(System.Windows.Point start)
        {
            return new Point(Convert.ToInt32(start.X), Convert.ToInt32(start.Y));
        }

        public static BitmapSource ConvertBitmap(Bitmap source)
        {
            return System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(
                          source.GetHbitmap(),
                          IntPtr.Zero,
                          System.Windows.Int32Rect.Empty,
                          BitmapSizeOptions.FromEmptyOptions());
        }

        public static Bitmap BitmapFromSource(BitmapSource bitmapsource)
        {
            Bitmap bitmap;
            using(var outStream = new MemoryStream())
            {
                BitmapEncoder enc = new BmpBitmapEncoder();
                enc.Frames.Add(BitmapFrame.Create(bitmapsource));
                enc.Save(outStream);
                bitmap = new Bitmap(outStream);
            }
            return bitmap;
        }
    }
}
