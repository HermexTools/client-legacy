using System.IO;

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
