package com.company.awms.data.forum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForumThreadRepo extends MongoRepository<ForumThread, String> {

	//Optional<ForumThread> findByTitle(String title);

	List<ForumThread> findByIssuerID(String issuerID);
}
