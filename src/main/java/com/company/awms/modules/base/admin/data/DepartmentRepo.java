package com.company.awms.modules.base.admin.data;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepo extends MongoRepository<Department, String> {
	Optional<Department> findByDepartmentCode(String name);
	Optional<Department> findByName(String name);
}
