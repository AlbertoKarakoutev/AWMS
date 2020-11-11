package com.company.awms.data.documents;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepo extends MongoRepository<Doc, String> {

	public Doc findByPath(String path);
	public ArrayList<Doc> findByAccessLevel(String accessLevel);
}
