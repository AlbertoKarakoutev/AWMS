package com.company.awms.services;

import com.company.awms.data.forum.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        return this.forumThreadRepo.findAll();
    }

    public List<ForumThread> getAllThreadsFromEmployee(String issuerID) {
        return this.forumThreadRepo.findByIssuerID(issuerID);
    }

    public List<ForumReply> getAllRepliesFromEmployee(String issuerID) {
        return this.forumReplyRepo.findByIssuerID(issuerID);
    }

    public void addNewThread(ForumThread newThread) {
        //TODO:
        //Validation? from Validator Class
        this.forumThreadRepo.save(newThread);
    }

    public void addNewReply(ForumReply newReply) {
        //TODO:
        //Validation? from Validator Class
        this.forumReplyRepo.save(newReply);
    }

    public void markAsAnswered(String threadID) throws IOException{
        ForumThread forumThread = getThread(threadID);

        forumThread.setAnswered(true);

        this.forumThreadRepo.save(forumThread);
    }

    public void editThread(ForumThread newForumThread, String oldThreadID) throws IOException{
        ForumThread oldThread = getThread(oldThreadID);
        //TODO:
        //Validation? from Validator Class
        //We don't update the issuerID, time and isAnswered because they are presumed to be the same.
        oldThread.setBody(newForumThread.getBody());
        oldThread.setTitle(newForumThread.getTitle());

        this.forumThreadRepo.save(oldThread);
    }
}
