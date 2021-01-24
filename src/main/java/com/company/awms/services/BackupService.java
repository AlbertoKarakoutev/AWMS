package com.company.awms.services;

import com.company.awms.backup.DataParser;
import com.company.awms.backup.FolderZipper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    private DataParser dataParser;
    private FolderZipper folderZipper;

    @Autowired
    public BackupService(DataParser dataParser, FolderZipper folderZipper) {
        this.dataParser = dataParser;
        this.folderZipper = folderZipper;
    }

    //@Scheduled(fixedRate = 2 * 1000)
    public void generateBackup(){
        System.out.println("Generating Backup!");
        try {
            String today = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now());
            Path backupFolderPath = Paths.get("backup").toAbsolutePath();
            File backupFolder = new File(backupFolderPath.toString());

            boolean isAlreadyCreated = getIsAlreadyCreated(backupFolder, today);

            if(!isAlreadyCreated){
                System.out.println("Saving Json...");
                Path databaseFilePath = Paths.get(backupFolder.toString(), today, "database.json");
                String json = this.dataParser.parseToJSON();
                writeJson(json, databaseFilePath.toString());
                System.out.println("Json saved!");

                System.out.println("Zipping Files...");
                String inputFolder = backupFolderPath.toFile().getParentFile().toString();
                String outputPath = Paths.get(backupFolder.toString(), today, "files.zip").toString();
                this.folderZipper.zipFolder(inputFolder, outputPath);
                System.out.println("File Zipped!");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean getIsAlreadyCreated(File backupFolder, String today){
        File[] listOfFolders = backupFolder.listFiles();
        boolean isAlreadyCreated = false;
        for (File dateFolder : listOfFolders) {
            if (dateFolder.isDirectory() && dateFolder.getName().equals(today)) {
                isAlreadyCreated = true;

                break;
            }
        }

        return isAlreadyCreated;
    }

    private void writeJson(String json, String fileName){
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try(PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print(json);
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found!");
        }
    }

    public void saveToGithub(){
        //TODO:
    }
    //Dangerous! Do it on a dummy database only!
    public void overwriteDatabase(){
        //TODO:
    }
}
