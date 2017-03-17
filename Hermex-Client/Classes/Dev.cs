using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Hermex.Classes
{
    public class Dev
    {
        public Bitmap Flag { get; set; }
        public string Github { get; set; }
        public string Nickname { get; set; }
        public string Fullname { get; set; }
        public bool Retired { get; set; }
        
        public Dev(Bitmap flag, string github, string nickname, string fullname=null, bool retired=false)
        {
            this.Flag = flag;
            this.Github = github;
            this.Nickname = nickname;
            this.Fullname = fullname;
            this.Retired = retired;
        }

        public string Label
        {
            get
            {
                return Nickname + " (" + Fullname + ")";
            }
        }

        public BitmapImage ImageFlag
        {
            get
            {
                return BitmapToImageSource(Flag);
            }
        }

        public BitmapImage GitImage
        {
            get
            {
                return BitmapToImageSource(Properties.Resources.github);
            }
        }

        public System.Windows.FontStyle LabelStyle
        {
            get
            {
                return Retired ? 
                    FontStyles.Italic : 
                    FontStyles.Normal;
            }
        }

        public SolidColorBrush LabelColor
        {
            get
            {
                return new SolidColorBrush(Retired ? Colors.Gray : Colors.Black);
            }
        }

        private BitmapImage BitmapToImageSource(Bitmap bitmap)
        {
            using(MemoryStream memory = new MemoryStream())
            {
                bitmap.Save(memory, System.Drawing.Imaging.ImageFormat.Png);
                memory.Position = 0;
                BitmapImage bitmapimage = new BitmapImage();
                bitmapimage.BeginInit();
                bitmapimage.StreamSource = memory;
                bitmapimage.CacheOption = BitmapCacheOption.OnLoad;
                bitmapimage.EndInit();

                return bitmapimage;
            }
        }

    }
}
