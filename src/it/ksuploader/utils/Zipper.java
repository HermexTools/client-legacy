package it.ksuploader.utils;

import it.ksuploader.dialogs.ProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    
    private final File[] file;
    
    public Zipper(File[] file) {
        this.file=file;
    }
    
    public String toZip() {
        
        if (!file[0].getName().endsWith(".zip")) {
            try {
                FileOutputStream fos = new FileOutputStream(new Environment().getTempDir() + "/KStemp.zip");
                ZipOutputStream zos = new ZipOutputStream(fos);
                FileInputStream fis = null;
                
                for(File f : this.file){
                    
                    if(f.isDirectory()){
                        for (File fileDir : f.listFiles() ){
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
                    }else{
                        fis = new FileInputStream(f);
                        System.out.println("[Zipper] File length: " + f.length());
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
            }
            
        }
        
        if (file[0].getName().endsWith(".zip")) {
            System.out.println("[Zipper] '" + file.toString() + "' already zipped");
            return file[0].getName() ;
        }
        
        System.out.println("[Zipper] Zipping finished: KStemp.zip");
        return new Environment().getTempDir() + "/KStemp.zip";
    }
    
}
