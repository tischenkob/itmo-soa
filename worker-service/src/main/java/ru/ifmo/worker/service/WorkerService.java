package ru.ifmo.worker.service;

import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface WorkerService {
	Optional<Worker> findBy(int id);

	Collection<Worker> findAll();

	Collection<Worker> findWith(Map<String, String[]> parameters);

	void update(Worker instance);

	boolean delete(Worker instance);

	void save(Worker instance);

}
