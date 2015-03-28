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
        
        public String toZip(){
                
                if (!file.getName().endsWith(".zip")) {
                        
                        FileInputStream fis = null;
                        try {
                                System.out.println("[Zipper] Zipping '" + file.getName() + "' to a file");
                                // File da comprimere
                                // Stream di input
                                fis = new FileInputStream(file);
                                // Stream di output file
                                FileOutputStream fos = new FileOutputStream(file.getName() + ".zip");
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
                                }       progressDialog.close();
                                zos.close();
                                fos.close();
                                fis.close();
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        } finally {
                                try {
                                        fis.close();
                                } catch (IOException ex) {
                                        ex.printStackTrace();
                                }
                        }
                } else {
                        System.out.println("[Zipper] '" + file.getName() + "' already zipped");
                        return file.getPath();
                }
                
                System.out.println("[Zipper] Zipping finished: " + file.getName() + ".zip");
                return file.getName() + ".zip";
        }
        
}
