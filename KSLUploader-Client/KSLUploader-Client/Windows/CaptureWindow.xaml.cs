using System;
using System.Linq;
using System.Windows;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;
using KSLUploader.Classes;

namespace KSLUploader.Windows
{
    public partial class CaptureWindow : Window
    {
        public static System.Drawing.Bitmap Capture()
        {
            var snap = new CaptureWindow();
            if (snap.ShowDialog() == true)
            {
                return snap.outputBitmap;
            }
            return null;
        }

        public System.Drawing.Bitmap outputBitmap = null;
        private Point startPoint;
        private Point endPoint;

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

            regionBackground.Rect = new Rect(0, 0, width + 2, height + 2);


            this.MouseRightButtonUp += CaptureWindow_MouseRightButtonUp;
            this.MouseDown += CaptureWindow_MouseDown;
            this.MouseMove += CaptureWindow_MouseMove;
            this.MouseUp += CaptureWindow_MouseUp;
            this.KeyUp += CaptureWindow_KeyUp;

        }

        #region CANCEL ACTION EVENTS

        private void CaptureWindow_KeyUp(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if(e.Key==Key.Escape)
            {
                CancelAction();
            }
        }

        private void CaptureWindow_MouseRightButtonUp(object sender, MouseButtonEventArgs e)
        {
            CancelAction();
        }

        private void CancelAction()
        {
            this.DialogResult = false;
            this.Close();
        }

        #endregion

        #region MOUSE DRAWING EVENTS

        private void CaptureWindow_MouseUp(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left)
            {
                startPoint = this.PointToScreen(startPoint);
                endPoint = this.PointToScreen(endPoint);

                Point start = new Point(
                    Math.Min(startPoint.X, endPoint.X),
                    Math.Min(startPoint.Y, endPoint.Y)
                    );

                Point end = new Point(
                    Math.Max(startPoint.X, endPoint.X) - 2,
                    Math.Max(startPoint.Y, endPoint.Y) - 2
                    );

                outputBitmap = Utils.GetBitmapFromPoints(Utils.FromWindowsToDrawingPoint(start), Utils.FromWindowsToDrawingPoint(end));
                this.DialogResult = true;
                this.Close();
            }
        }

        private void CaptureWindow_MouseMove(object sender, System.Windows.Input.MouseEventArgs e)
        {
            if (e.LeftButton == MouseButtonState.Pressed)
            {
                endPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void CaptureWindow_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left)
            {
                startPoint = e.GetPosition(this);
                endPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void DrawRegion()
        {
            regionSelection.Rect = new Rect(
                Math.Min(startPoint.X, endPoint.X),
                Math.Min(startPoint.Y, endPoint.Y),
                Math.Max(startPoint.X - endPoint.X, endPoint.X - startPoint.X),
                Math.Max(startPoint.Y - endPoint.Y, endPoint.Y - startPoint.Y)
                );
        }

        #endregion
    }
}
