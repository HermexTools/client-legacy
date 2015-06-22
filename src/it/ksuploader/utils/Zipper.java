package it.ksuploader.utils;

import it.ksuploader.main.Main;

import java.io.*;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
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
		try {

			Main.myLog("[Zipper] file.length: " + file.length);

			if (method.equals("socket")) // socket or ftp
				fileName = "KStemp.zip";
			else
				fileName = System.currentTimeMillis() / 1000 + new Random().nextInt(999) + ".zip";

			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(Main.so.getTempDir() + "/" + fileName));


			for (File f : this.file) {
				if (f.isDirectory()){
					zipDirectory(f, zos);
				} else {
					FileInputStream fis = new FileInputStream(f);
					zos.putNextEntry(new ZipEntry(f.getName()));
					byte[] bytes = new byte[1024];
					long count = 0;
					int length;
					Main.progressDialog.setMessage("Zipping...");
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
						count += length;
						Main.progressDialog.set((int) (count * 100 / f.length()));
					}
					zos.closeEntry();
				}
			}
			zos.flush();
			zos.close();

			Main.myLog("[Zipper] Zipping finished: " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return Main.so.getTempDir() + "/" + fileName;
	}

	private void zipDirectory(File directory, ZipOutputStream zout){
		try {
			URI base = directory.toURI();
			Deque<File> queue = new LinkedList<>();
			queue.push(directory);
				while (!queue.isEmpty()) {
					directory = queue.pop();
					for (File kid : directory.listFiles()) {
						String name = base.relativize(kid.toURI()).getPath();
						if (kid.isDirectory()) {
							queue.push(kid);
							name = name.endsWith("/") ? name : name + "/";
							zout.putNextEntry(new ZipEntry(name));
						} else {
							zout.putNextEntry(new ZipEntry(name));
							copy(kid, zout);
							zout.closeEntry();
						}
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copy(InputStream in, OutputStream out, long len) throws IOException {
		byte[] buffer = new byte[1024];
		long count = 0;
		int length;
		Main.progressDialog.setMessage("Zipping...");
		while ((length = in.read(buffer)) >= 0) {
			out.write(buffer, 0, length);
			count += length;
			Main.progressDialog.set((int) (count * 100 / len));
		}
	}

	private void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out, file.length());
		} finally {
			in.close();
		}
	}
}
