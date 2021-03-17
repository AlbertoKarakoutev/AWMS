package com.company.awms.modules.base.documents.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepo extends MongoRepository<Doc, String> {
	List<Doc> findByDepartment(String department);
	List<Doc> findByLimitedAccess(boolean limitedAccess);
}
