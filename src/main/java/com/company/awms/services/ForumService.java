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
    
    public ThreadReplyDTO getThreadWithRepliesById(String threadId) throws IOException {
        Optional<ForumThread> thread = this.forumThreadRepo.findById(threadId);

        if(thread.isEmpty()){
            throw new IOException("Thread not found!");
        }

        List<ForumReply> replies = this.forumReplyRepo.findByThreadId(threadId);

        return new ThreadReplyDTO(thread.get(), replies);
    }

    public List<ForumThread> getAllThreads() {
        return this.forumThreadRepo.findAll();
    }

    public List<ForumThread> getAllThreadsFromEmployee(String issuerId) {
        return this.forumThreadRepo.findByIssuerId(issuerId);
    }

    public List<ForumReply> getAllRepliesFromEmployee(String issuerId) {
        return this.forumReplyRepo.findByIssuerId(issuerId);
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
