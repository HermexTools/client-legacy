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

        private Queue<FileInfo> queue;

        private TcpClient socket;

        private static SocketUploader instance;

        public static SocketUploader Instance
        {
            get
            {
                if(instance == null)
                {
                    instance = new SocketUploader();
                }
                return instance;
            }
        }

        private SocketUploader()
        {
            queue = new Queue<FileInfo>();
        }

        public string SendNext()
        {

            //FileInfo fileToSend = queue.First();

            socket = new TcpClient("sergix44.ovh", 4030);

            //FileStream reader = new FileStream(fileToSend.FullName, FileMode.Open);

            StreamWriter output = new StreamWriter(socket.GetStream(), Encoding.UTF8);
            StreamReader input = new StreamReader(socket.GetStream(), Encoding.UTF8);
            output.NewLine = "\n";


            output.WriteLine("ilmioksu&123&txt");
            output.Flush();
            Console.WriteLine(input.ReadLine());
            return "";
        }
        



    }
}
