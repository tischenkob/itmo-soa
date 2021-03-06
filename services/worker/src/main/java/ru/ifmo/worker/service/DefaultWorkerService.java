package ru.ifmo.worker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.ifmo.util.query.Group;
import ru.ifmo.util.query.QueryParameters;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.repo.WorkerRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultWorkerService implements WorkerService {
	private final WorkerRepository repository;

	@Override
	public Optional<Worker> findBy(int id) {
		return repository.findBy(id);
	}

	@Override
	public Collection<Worker> findWith(QueryParameters parameters) {
		if (parameters.isEmpty()) {
			return repository.findAll();
		}
		return repository.findWith(parameters);
	}

	@Override
	public void update(Worker instance) {
		try {
			boolean operationSuccessful = repository.update(instance);
			if (!operationSuccessful) {
				throw new IllegalArgumentException("Validation violation for worker " + instance.getName());
			}
		} catch (DataAccessException e) {
			System.err.println(e.getMessage());
			throw new IllegalArgumentException("Worker does not exist or validation violation for " +
			                                   instance.getName());
		}
	}

	@Override
	public void deleteBy(int id) {
		boolean operationSuccessful = repository.deleteBy(id);
		if (!operationSuccessful) {
			throw new NoSuchElementException("No element with id " + id + " found");
		}
	}

	@Override
	public void save(Worker instance) {
		try {
			boolean operationSuccessful = repository.save(instance);
			if (!operationSuccessful) {
				throw new IllegalArgumentException("Validation violation for worker " +
				                                   instance.getName());
			}
		} catch (DuplicateKeyException e) {
			System.err.println(e.getMessage());
			throw new IllegalArgumentException("Worker with passport " +
			                                   instance.getPerson().getPassport() +
			                                   " already exists");
		}
	}

	@Override
	public List<Group> countGrouped() {
		return repository.countGrouped();
	}

	@Override
	public EnumSet<Worker.Status> findDistinctStatuses() {
		return repository.findDistinctStatusValues()
		                 .stream()
		                 .map(String::toUpperCase)
		                 .map(toWorkerStatus())
		                 .collect(toStatusEnumSet());
	}

	@Override
	public Collection<Worker> findNamedLike(String substring) {
		return repository.findNamedLike(substring);
	}

	private Function<String, Worker.Status> toWorkerStatus() {
		return Worker.Status::valueOf;
	}

	private Collector<Worker.Status, ?, EnumSet<Worker.Status>> toStatusEnumSet() {
		return Collectors.toCollection(() -> EnumSet.noneOf(Worker.Status.class));
	}
}