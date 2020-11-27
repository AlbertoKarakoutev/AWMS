package com.company.awms.data.documents;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepo extends MongoRepository<Doc, String> {

	//Doc findByData(Binary data);
	List<Doc> findByDepartment(String department);
}
