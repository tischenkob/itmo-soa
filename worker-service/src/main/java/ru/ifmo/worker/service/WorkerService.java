package ru.ifmo.worker.service;

import ru.ifmo.util.query.QueryParameter;
import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.Optional;

public interface WorkerService {
	Optional<Worker> findBy(int id);

	Collection<Worker> findWith(Collection<QueryParameter> parameters);

	void update(Worker instance);

	void deleteBy(int id);

	void save(Worker instance);

}
