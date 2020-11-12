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
    
    public ThreadReplyDTO getThreadWithRepliesByID(String threadID) throws IOException {
        Optional<ForumThread> thread = this.forumThreadRepo.findById(threadID);

        if(thread.isEmpty()){
            throw new IOException("Thread not found!");
        }

        List<ForumReply> replies = this.forumReplyRepo.findByThreadID(threadID);

        return new ThreadReplyDTO(thread.get(), replies);
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
        //Validation? from Validator Class
        this.forumThreadRepo.save(newThread);
    }

    public void addNewReply(ForumReply newReply) {
        //Validation? from Validator Class
        this.forumReplyRepo.save(newReply);
    }


}
