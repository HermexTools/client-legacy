import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

	private final String fileName;

	public Zipper(String fileName) {
		this.fileName = fileName;
	}

	public boolean toZip() throws IOException {
		System.out.println("Zipping '" + fileName + "' to a file");

		// File da comprimere
		File file = new File(fileName);
		// Stream di input
		FileInputStream fis = new FileInputStream(file);

		// Stream di output file
		FileOutputStream fos = new FileOutputStream(fileName + ".zip");
		// Stream di output zip
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.close();
		fos.close();
		fis.close();

		System.out.println("Zipping finished");
		return true;
	}

}
