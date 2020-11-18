package com.company.awms.data.schedule;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepo extends MongoRepository<Day, String> {

	Optional<Day> findByDate(LocalDate date);

}
