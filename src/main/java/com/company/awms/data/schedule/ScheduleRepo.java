package com.company.awms.data.schedule;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;

public interface ScheduleRepo extends MongoRepository<Day, String> {

	public Day findByDate(LocalDate date);

}
