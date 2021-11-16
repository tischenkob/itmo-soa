package ru.ifmo.worker.service;

import ru.ifmo.util.query.Group;
import ru.ifmo.util.query.QueryParameters;
import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface WorkerService {
	Optional<Worker> findBy(int id);

	Collection<Worker> findWith(QueryParameters parameters);

	void update(Worker instance);

	void deleteBy(int id);

	void save(Worker instance);

	List<Group> countGrouped();

	EnumSet<Worker.Status> findDistinctStatuses();

	Collection<Worker> findNamedLike(String substring);
}
