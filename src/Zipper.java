import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

	private final File file;

	public Zipper(File file) {
		this.file = file;
	}

	public String toZip() throws IOException {
		System.out.println("[Zipper] Zipping '" + file.getName() + "' to a file");

		// File da comprimere
		// Stream di input
		FileInputStream fis = new FileInputStream(file);

		// Stream di output file
		FileOutputStream fos = new FileOutputStream(file.getName().split("\\.")[0] + ".zip");
		// Stream di output zip
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry zipEntry = new ZipEntry(file.getName());
		long fileLength = file.length();
		System.out.println("[Zipper] File length: " + fileLength);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		long count = 0;
		int length;
		ProgressDialog progressDialog = new ProgressDialog();
		progressDialog.setMessage("Comprimendo...");

		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
			count += length;
			progressDialog.set((int) (count * 100 / fileLength));
		}

		zos.close();
		fos.close();
		fis.close();

		System.out.println("[Zipper] Zipping finished: " + file.getName().split("\\.")[0] + ".zip");
		return file.getName().split("\\.")[0] + ".zip";
	}

}
