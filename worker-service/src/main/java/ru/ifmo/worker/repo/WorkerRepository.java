package ru.ifmo.worker.repo;

import ru.ifmo.util.query.QueryParameter;
import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.Optional;

public interface WorkerRepository {

	Optional<Worker> findBy(int id);

	Collection<Worker> findAll();

	Collection<Worker> findWith(Collection<QueryParameter> parameters);

	void save(Worker instance);

	void deleteBy(int id);
}
