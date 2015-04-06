package it.ksuploader.utils;

import it.ksuploader.dialogs.ProgressDialog;
import it.ksuploader.main.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

	private final File[] file;

	public Zipper(File[] file) {
		this.file = file;
	}

	public String toZip(String method) {

		String fileName = null;
		Main.myLog("[Zipper] file.length: " + file.length);
		try {
			FileOutputStream fos;

			if (method.equals("socket")) // socket or ftp
				fileName = "KStemp.zip";
			else
				fileName = System.currentTimeMillis() / 1000 + new Random().nextInt(999) + ".zip";

			fos = new FileOutputStream(new Environment().getTempDir() + "/" + fileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			FileInputStream fis = null;

			for (File f : this.file) {

				if (f.isDirectory()) {
					for (File fileDir : f.listFiles()) {
						fis = new FileInputStream(fileDir);
						zos.putNextEntry(new ZipEntry(fileDir.getName()));
						byte[] bytes = new byte[1024];
						long count = 0;
						int length;
						ProgressDialog progressDialog = new ProgressDialog();
						progressDialog.setMessage("Comprimendo...");
						while ((length = fis.read(bytes)) >= 0) {
							zos.write(bytes, 0, length);
							count += length;
							progressDialog.set((int) (count * 100 / fileDir.length()));
						}
						progressDialog.close();

					}
				} else {
					fis = new FileInputStream(f);
					Main.myLog("[Zipper] File length: " + f.length());
					zos.putNextEntry(new ZipEntry(f.getName()));
					byte[] bytes = new byte[1024];
					long count = 0;
					int length;
					ProgressDialog progressDialog = new ProgressDialog();
					progressDialog.setMessage("Comprimendo...");
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
						count += length;
						progressDialog.set((int) (count * 100 / f.length()));
					}
					progressDialog.close();

				}
			}
			zos.close();
			fos.close();
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
		}
		Main.myLog("[Zipper] Zipping finished: " + fileName);
		return new Environment().getTempDir() + "/" + fileName;
	}
}
