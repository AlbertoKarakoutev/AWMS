package com.company.awms.services;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.forum.*;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.util.ForumComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ForumService {
    private ForumThreadRepo forumThreadRepo;
    private ForumReplyRepo forumReplyRepo;

    @Autowired
    public ForumService(ForumThreadRepo forumThreadRepo, ForumReplyRepo forumReplyRepo) {
        this.forumThreadRepo = forumThreadRepo;
        this.forumReplyRepo = forumReplyRepo;
    }

    public ForumThread getThread(String threadID) throws IOException{
        Optional<ForumThread> thread = this.forumThreadRepo.findById(threadID);

        if(thread.isEmpty()){
            throw new IOException("Thread not found!");
        }

        return thread.get();
    }
    
    public ThreadReplyDTO getThreadWithRepliesByID(String threadID) throws IOException {
        ForumThread thread = getThread(threadID);

        List<ForumReply> replies = this.forumReplyRepo.findByThreadID(threadID);

        return new ThreadReplyDTO(thread, replies);
    }

    public List<ForumThread> getAllThreads() {
        List<ForumThread> allThreads = this.forumThreadRepo.findAll();
        allThreads.sort(new ForumComparator());

        return allThreads;
    }

    public List<ForumThread> getAllAnsweredThreads() {
        List<ForumThread> allThreads = getAllThreads();
        allThreads.removeIf(t -> !t.getAnswered());

        return allThreads;
    }

    public List<ForumThread> getAllUnansweredThreads() {
        List<ForumThread> allThreads = getAllThreads();
        allThreads.removeIf(ForumThread::getAnswered);

        return allThreads;
    }

    public List<ForumThread> getAllThreadsFromEmployee(String issuerID) {
        List<ForumThread> employeeThreads = this.forumThreadRepo.findByIssuerID(issuerID);
        employeeThreads.sort(new ForumComparator());

        return employeeThreads;
    }

    public List<ForumReply> getAllRepliesFromEmployee(String issuerID) {
        return this.forumReplyRepo.findByIssuerID(issuerID);
    }

    public ForumThread addNewThread(EmployeeDetails employeeDetails, String title, String body) {
        //TODO:
        //Validation? from Validator Class
        ForumThread newThread = new ForumThread(employeeDetails.getID(), body, title, LocalDateTime.now(),
                false, employeeDetails.getFirstName() + " " + employeeDetails.getLastName());

        this.forumThreadRepo.save(newThread);

        return newThread;
    }

    public void addNewReply(EmployeeDetails employeeDetails, String body, String threadID) {
        //TODO:
        //Validation? from Validator Class
        ForumReply newReply = new ForumReply(threadID, employeeDetails.getID(), body, LocalDateTime.now(),
                employeeDetails.getFirstName() + " " + employeeDetails.getLastName());

        this.forumReplyRepo.save(newReply);
    }

    public void markAsAnswered(ForumThread forumThread) {
        forumThread.setAnswered(true);

        this.forumThreadRepo.save(forumThread);
    }

    public ForumThread editThread(String body, String title, ForumThread oldThread) {
        //TODO:
        //Validation? from Validator Class
        //We don't update the issuerID, time and isAnswered because they are presumed to be the same.
        oldThread.setBody(body);
        oldThread.setTitle(title);
        oldThread.setDateTime(LocalDateTime.now());

        this.forumThreadRepo.save(oldThread);

        return oldThread;
    }
}
