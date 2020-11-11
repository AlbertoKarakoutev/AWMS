package com.company.awms.data.documents;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepo extends MongoRepository<File, String> {

	public File findByPath(String path);
	public ArrayList<File> findByAccessLevel(String accessLevel);
}
