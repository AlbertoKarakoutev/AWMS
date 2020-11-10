package com.company.awms.data.schedule;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepo extends MongoRepository<Day, String> {

	public Day findByDate(LocalDate date);

}
