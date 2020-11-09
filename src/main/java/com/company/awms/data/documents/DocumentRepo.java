package com.company.awms.data.documents;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepo extends MongoRepository<File, String> {

	//public File findByID(String id);

	public File findByPath(String path);

	public List<File> findByAccessLevel(String accessLevel);


}