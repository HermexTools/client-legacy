import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Sergio
 */
public class LoadConfig {

	private String ip;
	private String pass;

	public LoadConfig() throws IOException {
		Properties prop = new Properties();

		if (!new File("config.properties").exists()) {
			prop.setProperty("server_ip", "localhost");
			prop.setProperty("password", "pass");
			prop.store(new FileOutputStream("config.properties"), null);
		}
		InputStream inputStream = new FileInputStream("config.properties");
		prop.load(inputStream);

		this.ip = prop.getProperty("server_ip");
		this.pass = prop.getProperty("password");
	}

	public String getIp() {
		return ip;
	}
        
	public String getPass() {
		return pass;
	}

}
