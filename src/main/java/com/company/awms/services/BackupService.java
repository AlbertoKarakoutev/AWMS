package com.company.awms.services;

import com.company.awms.backup.DataParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public BackupService(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    //@Scheduled(fixedRate = 2 * 1000)
    public void generateBackup(){

        System.out.println("Generating Backup!");
        try {
            String today = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now());
            Path backupFolderPath = Paths.get("backup").toAbsolutePath();

            String json = this.dataParser.parseToJSON();

            File backupFolder = new File(backupFolderPath.toString());
            File[] listOfFolders = backupFolder.listFiles();
            boolean alreadyCreated = false;
            for (File dateFolder : listOfFolders) {
                if (dateFolder.isDirectory() && dateFolder.getName().equals(today)) {
                    alreadyCreated = true;

                    break;
                }
            }

            if(!alreadyCreated){
                Path filePath = Paths.get(backupFolder.toString(), today, "database.json");
                writeJson(json, filePath.toString());
                //writeFilesArchive()
            }
        } catch (JsonProcessingException e) {
            System.out.println("Database Backup failed to generate!");
            System.out.println(e.getMessage());
        }
    }

    private void writeJson(String json, String fileName){
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            PrintWriter printWriter = new PrintWriter(file);
            printWriter.print(json);

            printWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found!");
        }
    }

    private void writeFilesArchive(){
        //TODO:
    }
    private void uploadBackup(){}
    //Dangerous! Do it on a dummy database only!
    public void overwriteDatabase(){
        //TODO:
    }
}
