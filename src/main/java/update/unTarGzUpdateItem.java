package update;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 08.03.2016.
 */
public class unTarGzUpdateItem extends updateItem {
    private File sourceDir;
    private String relativeSourceDir;
    private String destDir;

    public unTarGzUpdateItem(String ID, File sourceDir, String relativeSourceDir, String destDir) {
        super(ID, updateConstants.UNTARGZ);
        this.sourceDir = sourceDir;
        this.relativeSourceDir = relativeSourceDir;
        this.destDir = destDir;
    }

    @Override
    public boolean execute() {
        Path source = Paths.get(sourceDir.getAbsolutePath(), relativeSourceDir, super.getID());
        if (!source.toFile().exists()) return false;
        byte[] buffer = new byte[1024];
        File folder = Paths.get(source.getParent().toString(), destDir).toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        TarArchiveInputStream tarInput;
        try {
            tarInput = new TarArchiveInputStream(new java.util.zip.GZIPInputStream(new FileInputStream(source.toFile())));
            TarArchiveEntry te;
            while ((te = tarInput.getNextTarEntry()) != null) {
                String fileName = te.getName();
                File newFile = new File(folder + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                if (te.isDirectory()) {
                    newFile.mkdir();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = tarInput.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
            }
            tarInput.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
