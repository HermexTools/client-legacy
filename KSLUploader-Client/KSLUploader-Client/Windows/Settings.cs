using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace KSLUploader_Client.Windows
{
    public partial class Settings : Form
    {
        public Settings()
        {
            InitializeComponent();

            //read settings and apply to controls
            use_ftp.Checked = Properties.Settings.Default.use_ftp;
            use_ftps.Checked = Properties.Settings.Default.use_ftps;
            save_image.Checked = Properties.Settings.Default.save_image;
            open_startup.Checked = Properties.Settings.Default.open_startup;

            ftp_address.Text = Properties.Settings.Default.ftp_address;
            ftp_port.Text = Properties.Settings.Default.ftp_port;
            ftp_directory.Text = Properties.Settings.Default.ftp_directory;
            ftp_weburl.Text = Properties.Settings.Default.ftp_weburl;
            ftp_user.Text = Properties.Settings.Default.ftp_user;
            ftp_password.Text = Properties.Settings.Default.ftp_password;
            ftp_certificate.Checked = Properties.Settings.Default.ftp_certificate;

            server_address.Text = Properties.Settings.Default.server_address;
            server_password.Text = Properties.Settings.Default.server_password;
            server_port.Text = Properties.Settings.Default.server_port;

            //TODO: shortcut button


            //check controls logic
            CheckControlsLogic();


        }

        private void cancelButton_Click(object sender, EventArgs e)
        {
            //close window
            this.Close();
        }

        private void okButton_Click(object sender, EventArgs e)
        {
            //save settings
            Properties.Settings.Default["use_ftp"] = use_ftp.Checked;
            Properties.Settings.Default["use_ftps"] = use_ftps.Checked;
            Properties.Settings.Default["save_image"] = save_image.Checked;
            Properties.Settings.Default["open_startup"] = open_startup.Checked;

            Properties.Settings.Default["ftp_address"] = ftp_address.Text;
            Properties.Settings.Default["ftp_port"] = ftp_port.Text;
            Properties.Settings.Default["ftp_directory"] = ftp_directory.Text;
            Properties.Settings.Default["ftp_weburl"] = ftp_weburl.Text;
            Properties.Settings.Default["ftp_user"] = ftp_user.Text;
            Properties.Settings.Default["ftp_password"] = ftp_password.Text;
            Properties.Settings.Default["ftp_certificate"] = ftp_certificate.Checked;

            Properties.Settings.Default["server_address"] = server_address.Text;
            Properties.Settings.Default["server_password"] = server_password.Text;
            Properties.Settings.Default["server_port"] = server_port.Text;

            //TODO: shortcut button


            //close window
            this.Close();

        }

        private void CheckControlsLogic()
        {
            use_ftps.Enabled = false;

            ftp_address.Enabled = false;
            ftp_port.Enabled = false;
            ftp_directory.Enabled = false;
            ftp_weburl.Enabled = false;
            ftp_user.Enabled = false;
            ftp_password.Enabled = false;
            ftp_certificate.Enabled = false;

            server_address.Enabled = false;
            server_password.Enabled = false;
            server_port.Enabled = false;            

            if(use_ftp.Checked)
            {
                use_ftps.Enabled = true;

                ftp_address.Enabled = true;
                ftp_port.Enabled = true;
                ftp_directory.Enabled = true;
                ftp_weburl.Enabled = true;
                ftp_user.Enabled = true;
                ftp_password.Enabled = true;
                ftp_certificate.Enabled = true;
            }
            else
            {
                server_address.Enabled = true;
                server_password.Enabled = true;
                server_port.Enabled = true;
            }
        }

        private void use_ftp_CheckedChanged(object sender, EventArgs e)
        {
            CheckControlsLogic();
        }
    }
}
