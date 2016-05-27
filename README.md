## KSUploader-client
KSUploader is a instant sharing tool. Inspired by Puush and ScreenCloud, with this tool you can share instantly:
* a partial screen
* a complete desktop screen
* a file (that will be zipped)
* an entire file set (that will be zipped)
* the clipboard
obtaining immediately the direct sharing URL.

You can host all files in your server (via dedicated socket [server application](https://github.com/KSUploader/KSUploader-server)) or web hosting space (via FTP).

Since this project is open source and java written, feel free to fork, give suggestions or make pull requests.

### How to install
Actually simply run the binary file where you want, and it will save the configurations in the correct place.

### How to configure
Open KSUploader.jar, right-click on icon in the system tray, settings, and setup your client.

OR

Open and edit the `client.properties` file saved in `Appdata\Roaming\.ksuploader` on Windows, in your user home in the folder `.ksuploader` on Linux or in `~/Library/Application Support/.ksuploader` on OS X, then launch the client again.
It contains:
* `server_address`: the address where there is the server, you can use a domain (example.com) or an ip.
* `password`: the password of the server that will be used during the authentication check.
* `port`: the port where there is the running server.
* `ftp_enabled`: set this "true" or "false" to enable o disable the FTP feature, if on "true" the socket config will be ignored.
* `ftp_address`: the address for the FTP connection (es. ftp.mydomain.com).
* `ftp_user`: the FTP user.
* `ftp_password`: the FTP password.
* `ftp_port`: the port where the FTP is listening (21 as default).
* `ftp_directory`: the relative directory how to upload all files, use a dot (.) to say "this directory". To use a subfolder follow this example: "subFolder/anotherFolder".
* `ftp_weburl`: the url that will be returned, write a / at the end of the it.
* `save_enabled`: you can choose if save locally the pics taken, set it as true or false.
* `save_dir`: the folder where all images will be saved, use a . to say "where the client is located".
* `ftpes_enabled`: enable or disable (with "true" or "false") the FTPES.
* `accept_all_certificates`: choose to accept all certificates or not (true/false).
* `open_at_startup_enabled`: enable the autorun at os boot (Only windows).

>Attention: all config properties must be not null, even if you don't use one of them.

>Disclaimer: this is a project in early development stage. Since that it may change at any time.
