package it.ksuploader.utils;

import it.ksuploader.main.Main;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.Random;

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

			ZipFile zp = new ZipFile(Main.so.getTempDir() + "/" + fileName);
			zp.setRunInThread(true);

			ZipParameters zipParameters = new ZipParameters();
			zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

			for (File f : this.file) {
				if (f.isDirectory()){
					zp.addFolder(f, zipParameters);
					ProgressMonitor pm = zp.getProgressMonitor();

					while (pm.getState() == ProgressMonitor.STATE_BUSY) {
						Main.dialog.set(pm.getPercentDone());
					}
				} else {
					zp.addFile(f, zipParameters);
					ProgressMonitor pm = zp.getProgressMonitor();

					while (pm.getState() == ProgressMonitor.STATE_BUSY) {
						Main.dialog.set(pm.getPercentDone());
					}
				}
			}

			Main.myLog("[Zipper] Zipping finished: " + fileName);

		} catch (ZipException e) {
			e.printStackTrace();
		}
		return Main.so.getTempDir() + "/" + fileName;
	}
}
