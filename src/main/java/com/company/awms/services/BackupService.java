package com.company.awms.services;

import com.company.awms.backup.Credentials;
import com.company.awms.backup.DataParser;
import com.company.awms.backup.FolderZipper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class BackupService {

    private DataParser dataParser;
    private FolderZipper folderZipper;

    @Autowired
    public BackupService(DataParser dataParser, FolderZipper folderZipper) {
        this.dataParser = dataParser;
        this.folderZipper = folderZipper;
    }

    //@Scheduled(fixedRate = 60 * 1000)
    @Scheduled(fixedRate = 86400 * 1000)
    public void generateBackup(){
        System.out.println("Checking for backup...");
        try {
            String today = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now());
            Path backupFolderPath = Paths.get("backup").toAbsolutePath();
            File backupFolder = new File(backupFolderPath.toString());

            boolean isAlreadyCreated = getIsAlreadyCreated(backupFolder, today);

            if(!isAlreadyCreated){
                System.out.println("Generating Backup...");
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

                System.out.println("Pushing to github...");
                saveToGithub(today, inputFolder);
                System.out.println("Pushed Successfully!");
            } else {
                System.out.println("Backup is available!");
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

    private void saveToGithub(String today, String inputFolder){
        try {
            Repository localRepo = new FileRepository(inputFolder + "/.git");
            Git git = new Git(localRepo);
            String oldBranch = localRepo.getBranch();
            git.checkout().setCreateBranch(false).setName("backup").call();
            git.add().addFilepattern(".").call();
            git.add().setUpdate(true).addFilepattern(".").call();
            git.commit().setMessage("backup " + today).call();
            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(Credentials.USERNAME, Credentials.PASSWORD)).call();
            git.checkout().setCreateBranch(false).setName(oldBranch).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStatus(Git git) throws GitAPIException {
        Status status = git.status().call();

        Set<String> added = status.getAdded();
        for (String add : added) {
            System.out.println("Added: " + add);
        }
        Set<String> uncommittedChanges = status.getUncommittedChanges();
        for (String uncommitted : uncommittedChanges) {
            System.out.println("Uncommitted: " + uncommitted);
        }

        Set<String> untracked = status.getUntracked();
        for (String untrack : untracked) {
            System.out.println("Untracked: " + untrack);
        }
    }
}
