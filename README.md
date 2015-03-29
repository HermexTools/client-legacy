## KSUploader-client
KSUploader is a instant sharing tool. Inspired by Puush and ScreenCloud, with this tool you can share instantly:
* a partial screen
* a complete desktop screen
* a file (that will be zipped)
* the clipboard
obtaining immediatly the direct sharing URL.

Since that this project is open source and java written, feel free to fork, give suggestions or make pull requests.

### How to install
Actually simply run one time to generate the `client.properties`: a config file.
To run the client you can use: `java -jar KSUploader_client.jar`

### How to configure
Open and edit the `client.properties` file, then launch the client again.
It contains:
* `server_ip`: the address where there is the server, you can use a domain (example.com) or an ip.
* `password`: the password of the server that will be used during the authentication check.
* `port`: the port where there is the running server.