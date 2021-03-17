package com.company.awms.modules.base.forum.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForumThreadRepo extends MongoRepository<ForumThread, String> {

	//Optional<ForumThread> findByTitle(String title);
	List<ForumThread> findByDepartment(String department);
	List<ForumThread> findByIssuerID(String issuerID);
	List<ForumThread> findByLimitedAccess(boolean limitedAccess);
}
