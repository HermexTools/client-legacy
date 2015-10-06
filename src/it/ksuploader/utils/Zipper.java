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

    public static String toZip(String method, File[] files) {
        String fileName = null;
        ZipOutputStream zos;
        try {

            Main.myLog("[Zipper] file.length: " + files.length);

            if (method.equals("socket")) // socket or ftp
                fileName = "KStemp.zip";
            else
                fileName = System.currentTimeMillis() / 1000 + new Random().nextInt(999) + ".zip";

            zos = new ZipOutputStream(new FileOutputStream(Main.so.getTempDir() + File.separator + fileName));
            Main.dialog.setButtonClickable(false);
            add(zos, files);
            Main.dialog.setButtonClickable(true);
            zos.flush();
            zos.close();
        } catch (IOException e) {
            Main.dialog.setButtonClickable(true);
            e.printStackTrace();
            Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
        Main.myLog("[Zipper] Zipping finished: " + Main.so.getTempDir() + File.separator + fileName);
        return Main.so.getTempDir() + File.separator + fileName;
    }

    private static void add(ZipOutputStream zout, File[] fileSource) {

        for (File f : fileSource) {
            if (f.isDirectory()) {
                add(zout, f.listFiles());
            } else {
                try (FileInputStream fin = new FileInputStream(f)) {
                    byte[] buffer = new byte[4096];

                    zout.putNextEntry(new ZipEntry(f.getName()));

                    int length;
                    long count = 0;

                    while ((length = fin.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                        count += length;
                        Main.dialog.set((int) (count * 100 / f.length()));
                    }
                    zout.closeEntry();

                } catch (IOException e) {
                    Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
                }
            }
        }

    }
}
