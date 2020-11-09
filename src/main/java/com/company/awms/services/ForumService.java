package com.company.awms.services;

import com.company.awms.data.forum.ForumRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumService {
    private ForumRepo forumRepo;

    @Autowired
    public ForumService(ForumRepo forumRepo) {
        this.forumRepo = forumRepo;
    }
}
