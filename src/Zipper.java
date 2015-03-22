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
    
    public boolean toZip() throws IOException {
        System.out.println("Zipping '" + file.getName() + "' to a file");
        
        // File da comprimere
        // Stream di input
        FileInputStream fis = new FileInputStream(file);
        
        // Stream di output file
        FileOutputStream fos = new FileOutputStream(file.getName().split("\\.")[0] + ".zip");
        // Stream di output zip
        ZipOutputStream zos = new ZipOutputStream(fos);
        
        ZipEntry zipEntry = new ZipEntry(file.getName());
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
