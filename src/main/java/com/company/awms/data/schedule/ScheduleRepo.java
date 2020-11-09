package com.company.awms.data.schedule;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepo extends MongoRepository<Day, String> {

	public Day findByDate(String date);

}
