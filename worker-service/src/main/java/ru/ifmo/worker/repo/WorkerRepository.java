package ru.ifmo.worker.repo;

import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.Optional;

public interface WorkerRepository {

	Optional<Worker> findBy(int id);

	Collection<Worker> findAll();

	Collection<Worker> find(int amount);

	Collection<Worker> find(int amount, int offset);

	void update(Worker instance, String field, Object value);

	void delete(Worker instance);

	void save(Worker instance);

}
