package com.company.awms.backup;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class FolderZipper {

    private List<String> fileList;

    public void zipFolder(String inputFolder, String outputPath) {
        this.fileList = new ArrayList<>();

        generateFileList(new File(inputFolder), inputFolder);
        zip(inputFolder, outputPath, fileList);
    }

    private void zip(String inputFolder, String outputPath, List<String> fileList) {
        System.out.println(this.fileList.size());
        byte[] buffer = new byte[1024];
        String source = new File(inputFolder).getName();
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (String file: fileList) {
                ZipEntry zipEntry = new ZipEntry(source + File.separator + file);
                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream fileInputStream = new FileInputStream(inputFolder + File.separator + file)) {
                    int length;
                    while ((length = fileInputStream .read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                }
            }

            zipOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Ignoring backup folder and those starting with "."
    private void generateFileList(File file, String inputFolder) {
        if (file.isFile()) {
            this.fileList.add(generateZipEntry(file.toString(), inputFolder));
        }
        if (file.isDirectory() && !file.getName().equals("backup") && file.getName().charAt(0) != '.') {
            String[] subFile = file.list();
            for (String filename: subFile) {
                generateFileList(new File(file, filename), inputFolder);
            }
        }
    }

    private String generateZipEntry(String file, String inputFolder) {
        return file.substring(inputFolder.length() + 1);
    }
}
