using KSLUploader.Classes;
using System;
using System.Linq;
using System.Windows;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;

namespace KSLUploader.Windows
{
    public partial class CaptureWindow : Window
    {
        public bool hasCaught()
        {
            if(this.ShowDialog() == true)
            {
                return true;
            }
            return false;
        }

        public Point StartPoint, EndPoint;

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
            if (e.Key == Key.Escape)
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
                Point sp = this.PointToScreen(StartPoint);
                Point ep = this.PointToScreen(EndPoint);

                StartPoint = new Point(
                    Math.Min(sp.X, ep.X),
                    Math.Min(sp.Y, EndPoint.Y)
                    );

                EndPoint = new Point(
                    Math.Max(sp.X, ep.X) - 2,
                    Math.Max(sp.Y, ep.Y) - 2
                    );
                                
                this.DialogResult = true;
                this.Close();
            }
        }

        private void CaptureWindow_MouseMove(object sender, System.Windows.Input.MouseEventArgs e)
        {
            if (e.LeftButton == MouseButtonState.Pressed)
            {
                EndPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void CaptureWindow_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left)
            {
                StartPoint = e.GetPosition(this);
                EndPoint = e.GetPosition(this);
                DrawRegion();
            }
        }

        private void DrawRegion()
        {
            regionSelection.Rect = new Rect(
                Math.Min(StartPoint.X, EndPoint.X),
                Math.Min(StartPoint.Y, EndPoint.Y),
                Math.Max(StartPoint.X - EndPoint.X, EndPoint.X - StartPoint.X),
                Math.Max(StartPoint.Y - EndPoint.Y, EndPoint.Y - StartPoint.Y)
                );
        }

        #endregion
    }
}
