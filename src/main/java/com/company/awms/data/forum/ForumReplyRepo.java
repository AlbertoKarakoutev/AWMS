package com.company.awms.data.forum;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ForumReplyRepo extends MongoRepository<ForumReply, String> {

    List<ForumReply> findByThreadID(String threadID);

    List<ForumReply> findByIssuerID(String issuerID);

}
