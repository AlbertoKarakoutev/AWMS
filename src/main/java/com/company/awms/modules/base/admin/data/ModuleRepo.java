package com.company.awms.modules.base.admin.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModuleRepo extends MongoRepository<Module, String> {
	Optional<Module> findByName(String name);
	List<Module>findByBase(boolean base);
}
