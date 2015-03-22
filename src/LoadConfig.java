import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadConfig {

	private String ip;
	private String pass;
        private int port;

	public LoadConfig() throws IOException {
		Properties prop = new Properties();

		if (!new File("config.properties").exists()) {
			prop.setProperty("server_ip", "localhost");
                        prop.setProperty("port", "4030");
			prop.setProperty("password", "pass");
			prop.store(new FileOutputStream("config.properties"), null);
		}
		InputStream inputStream = new FileInputStream("config.properties");
		prop.load(inputStream);

		this.ip = prop.getProperty("server_ip");
		this.pass = prop.getProperty("password");
                this.port = Integer.parseInt(prop.getProperty("port"));
	}

	public String getIp() {
		return ip;
	}

	public String getPass() {
		return pass;
	}

        public int getPort() {
                return port;
        }
        

}
