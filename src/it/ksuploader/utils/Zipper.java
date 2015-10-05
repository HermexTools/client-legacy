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


    public static String toZip(String method,File[] files) {
        String fileName = null;
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        try {

            Main.myLog("[Zipper] file.length: " + files.length);

            if (method.equals("socket")) // socket or ftp
                fileName = "KStemp.zip";
            else
                fileName = System.currentTimeMillis() / 1000 + new Random().nextInt(999) + ".zip";

            zos = new ZipOutputStream(new FileOutputStream(Main.so.getTempDir() + "/" + fileName));
            Main.dialog.setButtonClickable(false);
            for (File f : files) {
                if (f.isDirectory()) {
                    addDirectory(zos, f);
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


        } catch (IOException e) {
            e.printStackTrace();
            Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        } finally {
            try {
                Main.dialog.setButtonClickable(true);
                fis.close();
                zos.flush();
                zos.close();
            } catch (IOException ex) {
                Main.myErr(Arrays.toString(ex.getStackTrace()).replace(",", "\n"));
            }
        }
        Main.myLog("[Zipper] Zipping finished: " + fileName);
        return Main.so.getTempDir() + "/" + fileName;
    }

    private static void addDirectory(ZipOutputStream zout, File fileSource) {
        File[] lfiles = fileSource.listFiles();

        for (File file1 : lfiles) {
            if (file1.isDirectory()) {
                addDirectory(zout, file1);
                continue;
            }
            try {

                byte[] buffer = new byte[1024];

                FileInputStream fin = new FileInputStream(file1);

                zout.putNextEntry(new ZipEntry(file1.getName()));

                int length;
                long count = 0;

                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                    count += length;
                    Main.dialog.set((int) (count * 100 / file1.length()));
                }
                zout.closeEntry();
                fin.close();

            } catch (IOException e) {
                Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
        }

    }
}
