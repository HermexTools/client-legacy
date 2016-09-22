using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KSLUploader.Classes.Uploaders
{
    class FtpUploader
    {
        private FileInfo file;

        public FtpUploader(FileInfo f)
        {
            file = f;
        }

        public bool Upload()
        {
            return false;
        }

        public void Stop()
        {

        }
    }
}
