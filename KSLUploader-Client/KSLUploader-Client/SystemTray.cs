using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace KSLUploader_Client
{
    class SystemTray
    {
        NotifyIcon ni;

        public SystemTray()
        {
            this.ni = new NotifyIcon();
        }
    }
}
