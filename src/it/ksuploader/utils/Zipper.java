package it.ksuploader.utils;

import it.ksuploader.main.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
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
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		try {

			Main.myLog("[Zipper] file.length: " + file.length);

			if (method.equals("socket")) // socket or ftp
				fileName = "KStemp.zip";
			else
				fileName = System.currentTimeMillis() / 1000 + new Random().nextInt(999) + ".zip";

			zos = new ZipOutputStream(new FileOutputStream(Main.so.getTempDir() + "/" + fileName));
			Main.dialog.setButtonClickable(false);
			for (File f : this.file) {
				if (f.isDirectory()){
					zipDirectory(f, zos);
				} else {
					fis = new FileInputStream(f);
					zos.putNextEntry(new ZipEntry(f.getName()));
					byte[] bytes = new byte[1024];
					long count = 0;
					int length;
					Main.dialog.show("Zipping...", "", false);
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
						count += length;
						Main.dialog.set((int) (count * 100 / f.length()));
					}
					fis.close();
					zos.closeEntry();
				}
			}

			zos.flush();
			zos.close();
			Main.dialog.setButtonClickable(true);
			Main.myLog("[Zipper] Zipping finished: " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
			try {
				Main.dialog.setButtonClickable(true);
				fis.close();
				zos.flush();
				zos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return Main.so.getTempDir() + "/" + fileName;
	}

	private void zipDirectory(File directory, ZipOutputStream zout){
		FileInputStream in = null;
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
							in = new FileInputStream(kid);
							byte[] buffer = new byte[1024];
							long count = 0;
							int length;
							Main.dialog.show("Zipping...", "", false);
							while ((length = in.read(buffer)) >= 0) {
								zout.write(buffer, 0, length);
								count += length;
								Main.dialog.set((int) (count * 100 / kid.length()));
							}
							in.close();
							zout.closeEntry();
						}
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
			Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
}
