using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using KSLUploader.Classes;

namespace KSLUploader.Windows
{
    public partial class CaptureWindow : Window
    {
        public static System.Drawing.Bitmap Capture()
        {
            var snap = new CaptureWindow();
            if(snap.ShowDialog() == true)
            {
                return snap.outputBitmap;
            }
            return null;
        }

        public System.Drawing.Bitmap outputBitmap = null;

        Point startPoint;
        Point endPoint;

        public CaptureWindow()
        {
            InitializeComponent();
            this.ShowInTaskbar = false;
            this.WindowStyle = WindowStyle.None;
            this.ResizeMode = ResizeMode.NoResize;
            this.WindowStartupLocation = WindowStartupLocation.Manual;
            this.WindowState = WindowState.Normal;
            this.Topmost = true;
            this.Background = new SolidColorBrush(Color.FromArgb(1, 0, 0, 0));
            this.AllowsTransparency = true;
            this.Cursor = System.Windows.Input.Cursors.Cross;

            var left = Screen.AllScreens.Min(screen => screen.Bounds.X);
            var top = Screen.AllScreens.Min(screen => screen.Bounds.Y);
            var right = Screen.AllScreens.Max(screen => screen.Bounds.X + screen.Bounds.Width);
            var bottom = Screen.AllScreens.Max(screen => screen.Bounds.Y + screen.Bounds.Height);
            var width = right - left;
            var height = bottom - top;

            this.Left = left;
            this.Top = top;
            this.Width = width;
            this.Height = height;
            
            regionBackground.Rect = new Rect(0, 0, width+2, height+2);

            this.MouseRightButtonUp += CaptureWindow_MouseRightButtonUp;
            this.MouseDown += CaptureWindow_MouseDown;
            this.MouseMove += CaptureWindow_MouseMove;
            this.MouseUp += CaptureWindow_MouseUp;

        }

        private void CaptureWindow_MouseUp(object sender, MouseButtonEventArgs e)
        {
            if(e.ChangedButton==MouseButton.Left)
            {
                endPoint.X -= 2;
                endPoint.Y -= 2;

                outputBitmap = Utils.GetBitmapFromPoints(Utils.FromWindowsToDrawingPoint(startPoint), Utils.FromWindowsToDrawingPoint(endPoint));
                this.DialogResult = true;
                this.Close();
            }
        }

        private void CaptureWindow_MouseMove(object sender, System.Windows.Input.MouseEventArgs e)
        {
            if(e.LeftButton == MouseButtonState.Pressed)
            {
                endPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void CaptureWindow_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if(e.ChangedButton == MouseButton.Left)
            {
                startPoint = new Point(0, 0);
                endPoint = new Point(0, 0);
                regionSelection.Rect = new Rect(0, 0, 0, 0);

                startPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void DrawRegion()
        {
            /*
            var width = endPoint.X > startPoint.X ? endPoint.X - startPoint.X : (endPoint.X - startPoint.X) * -1;
            var height = endPoint.Y > startPoint.Y ? endPoint.Y - startPoint.Y : (endPoint.Y - startPoint.Y) * -1;*/

            var width = endPoint.X > startPoint.X ? endPoint.X - startPoint.X : startPoint.X - endPoint.X;
            var height = endPoint.Y > startPoint.Y ? endPoint.Y - startPoint.Y : startPoint.Y - endPoint.Y;


            regionSelection.Rect = new Rect(startPoint.X, startPoint.Y, width, height);
        }

        private void CaptureWindow_MouseRightButtonUp(object sender, MouseButtonEventArgs e)
        {
            this.DialogResult = false;
            this.Close();
        }


    }
}
