package ru.ifmo.worker.repo;

import ru.ifmo.util.query.Group;
import ru.ifmo.util.query.QueryParameters;
import ru.ifmo.worker.model.Worker;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository {

	Optional<Worker> findBy(int id);

	Collection<Worker> findAll();

	List<Worker> findWith(QueryParameters parameters);

	boolean save(Worker instance);

	boolean deleteBy(int id);

	List<Group> countGrouped();

	boolean update(Worker instance);

	Collection<String> findDistinctStatusValues();

	Collection<Worker> findNamedLike(String substring);
}
