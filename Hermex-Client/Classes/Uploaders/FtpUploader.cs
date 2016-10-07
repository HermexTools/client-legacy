﻿using System;
using System.ComponentModel;
using System.IO;
using System.Net;

namespace Hermex.Classes.Uploaders
{
    class FtpUploader
    {
        private FtpWebRequest ftp;
        private FileInfo file;
        private string _link;
        private string _fileName;

        private BackgroundWorker worker;

        public FtpUploader(FileInfo f, BackgroundWorker bgworker, string sendname)
        {
            file = f;
            worker = bgworker;

            if(sendname.Contains("{0}"))
            {
                FilenameToSend = String.Format(sendname, Utils.GenerateName());
            }
            else
            {
                FilenameToSend = Utils.GenerateName(sendname);
            }
        }

        public bool Upload()
        {
            string ftpurl = String.Format("ftp://{0}:{1}{2}",
                SettingsManager.Get<string>("FTPAddress"),
                SettingsManager.Get<string>("FTPPort"),
                SettingsManager.Get<string>("FTPDirectory")
                );
            ftp = (FtpWebRequest)FtpWebRequest.Create(ftpurl + FilenameToSend);
            ftp.Credentials = new System.Net.NetworkCredential(SettingsManager.Get<string>("FTPUser"), SettingsManager.Get<string>("FTPPassword"));
            ftp.Method = WebRequestMethods.Ftp.UploadFile;
            ftp.UseBinary = true;
            ftp.KeepAlive = true;
            ftp.UsePassive = true;

            ftp.ContentLength = file.Length;

            int bufferSize = (int)Math.Min(4096, file.Length);
            byte[] buffer = new byte[bufferSize];
            int sentBytes = 0;

            int progress = 0;

            FileStream fs = file.OpenRead();
            Stream rs = ftp.GetRequestStream();

            while (sentBytes < file.Length)
            {
                int readed = fs.Read(buffer, 0, buffer.Length);
                rs.Write(buffer, 0, readed);

                sentBytes += readed;
                progress = (int)(100 * sentBytes / file.Length);

                worker.ReportProgress(progress);
            }

            fs.Close();
            rs.Close();
            FtpWebResponse uploadResponse = (FtpWebResponse)ftp.GetResponse();
            var value = uploadResponse.StatusDescription;
            uploadResponse.Close();

            Link = SettingsManager.Get<string>("FTPWeburl") + FilenameToSend;

            return true;
        }

        public void Stop()
        {
            ftp.Abort();
        }

        public string Link
        {
            get
            {
                return _link;
            }

            private set
            {
                _link = value;
            }
        }
        public string FilenameToSend
        {
            get
            {
                return _fileName;
            }

            private set
            {
                _fileName = value;
            }
        }
    }
}