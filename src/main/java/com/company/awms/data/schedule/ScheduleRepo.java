package com.company.awms.data.schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepo extends MongoRepository<Day, String> {

	Optional<Day> findByDate(LocalDate date);

	List<Day> findAllByDateBetween(LocalDate firstDay, LocalDate lastDay);
}
