package com.company.awms.data.forum;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForumRepo extends MongoRepository<ForumThread, String> {

	//public ForumThread findByID(String id);
	public ForumThread findByTitle(String title);
	public ArrayList<ForumThread> findByIssuerID(String issuerID);

}