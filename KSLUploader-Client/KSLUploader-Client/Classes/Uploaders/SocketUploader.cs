using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace KSLUploader.Classes.Uploaders
{
    public class SocketUploader
    {

        private enum Messages
        {
            OK,
            WRONG_PASSWORD,
            FILE_NOT_RECOGNIZED,
            FILE_TOO_LARGE,
            SERVER_FULL,
            UNKNOWN_ERROR
        }

        private TcpClient socket;
        private FileInfo file;

        public SocketUploader(FileInfo f)
        {
            file = f;
        }

        public bool Send()
        {

            socket = new TcpClient(Properties.Settings.Default.socket_address, Properties.Settings.Default.socket_port);

            DataOutputStream output = new DataOutputStream(new BinaryWriter(socket.GetStream()));
            DataInputStream input = new DataInputStream(new BinaryReader(socket.GetStream()));

            // password & file lenght & type
            output.WriteUTF(Properties.Settings.Default.socket_password + "&" + file.Length + "&" + getType(file));

            if (input.ReadUTF().Equals(Messages.OK.ToString()))
            {

                using (FileStream fileReader = new FileStream(file.FullName, FileMode.Open))
                {
                    int bufferSize = (int) Math.Min(4096, file.Length);
                    byte[] buffer = new byte[bufferSize];
                    int sentBytes = 0;

                    long progress = 0;

                    while (sentBytes < file.Length)
                    {
                        int readed = fileReader.Read(buffer, 0, bufferSize);
                        socket.GetStream().Write(buffer, 0, readed);

                        sentBytes += readed;
                        progress = (100 * sentBytes / file.Length);

                        Console.WriteLine(progress);
                    }
                }

                Console.Write(input.ReadUTF());
                file.Delete();
                socket.Close();

                return true;
            }

            if (input.ReadUTF().Equals(Messages.WRONG_PASSWORD.ToString()))
            {
                //display wrong password
            }

            if (input.ReadUTF().Equals(Messages.FILE_NOT_RECOGNIZED.ToString()))
            {
                //server doesn't recognize file type
            }

            if (input.ReadUTF().Equals(Messages.FILE_TOO_LARGE.ToString()))
            {
                //file too large
            }

            if (input.ReadUTF().Equals(Messages.SERVER_FULL.ToString()))
            {
                //server not have space
            }

            if (input.ReadUTF().Equals(Messages.UNKNOWN_ERROR.ToString()))
            {
                //unknow error happened
            }
            return false;
        }

        private string getType(FileInfo file)
        {
            switch (file.Extension)
            {
                case ".png":
                    return "img";
                case ".zip":
                    return "file";
                case ".txt":
                    return "txt";
                default:
                    return "unk";
            }
        }


    }
}
