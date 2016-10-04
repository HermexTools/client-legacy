using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Hermex.Windows
{
    /// <summary>
    /// Logica di interazione per UploadsPanel.xaml
    /// </summary>
    public partial class UploadsPanel : Window
    {

        const int WM_SYSCOMMAND = 0x0112;
        const int SC_MOVE = 0xF010;
        const int SC_RESTORE = 0xF120;

        public UploadsPanel()
        {
            InitializeComponent();
            Loaded += new RoutedEventHandler(PlaceRightBottom);
        }

        private void PlaceRightBottom(object sender, RoutedEventArgs e)
        {
            var desktopWorkingArea = System.Windows.SystemParameters.WorkArea;
            Left = desktopWorkingArea.Right - Width;
            Top = desktopWorkingArea.Bottom - Height;
        }

        private void UploadPanel_SourceInitialized(object sender, EventArgs e)
        {
            WindowInteropHelper helper = new WindowInteropHelper(this);
            HwndSource source = HwndSource.FromHwnd(helper.Handle);
            source.AddHook(WndProc);
        }

        private IntPtr WndProc(IntPtr hwnd, int msg, IntPtr wParam, IntPtr lParam, ref bool handled)
        {
            switch (msg)
            {
                case WM_SYSCOMMAND:
                    int command = wParam.ToInt32() & 0xfff0;
                    if (command == SC_MOVE)
                    {
                        // prevent user from moving the window
                        handled = true;
                    }
                    else if (command == SC_RESTORE && WindowState == WindowState.Maximized)
                    {
                        // prevent user from restoring the window while it is maximized
                        // (but allow restoring when it is minimized)
                        handled = true;
                    }
                    break;
                default:
                    break;
            }
            return IntPtr.Zero;
        }
    }
}
