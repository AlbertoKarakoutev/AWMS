package com.company.awms.data.documents;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeDocRepo extends MongoRepository<EmployeeDoc, String> {

    List<EmployeeDoc> findByUploaderID(String uploaderID);
}
