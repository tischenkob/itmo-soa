package ru.ifmo.worker.service;

import lombok.RequiredArgsConstructor;
import ru.ifmo.util.query.QueryParameter;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.repo.WorkerRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultWorkerService implements WorkerService {
	private final WorkerRepository repository;

	@Override
	public Optional<Worker> findBy(int id) {
		return repository.findBy(id);
	}

	@Override
	public Collection<Worker> findWith(Collection<QueryParameter> parameters) {
		if (parameters.isEmpty()) {
			return repository.findAll();
		}
		return repository.findWith(parameters);
	}

	@Override
	public void update(Worker instance) {

	}

	@Override
	public void deleteBy(int id) {
		repository.deleteBy(id);
	}

	@Override
	public void save(Worker instance) {
		repository.save(instance);
	}
}
