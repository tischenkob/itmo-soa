package ru.ifmo.worker.service;

import lombok.RequiredArgsConstructor;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.repo.WorkerRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultWorkerService implements WorkerService {
	private final WorkerRepository repository;

	@Override
	public Optional<Worker> findBy(int id) {
		return repository.findBy(id);
	}

	@Override
	public Collection<Worker> findAll() {
		return repository.findAll();
	}

	@Override
	public Collection<Worker> findWith(Map<String, String[]> parameters) {
		return null;
	}

	@Override
	public void update(Worker instance) {

	}

	@Override
	public boolean delete(Worker instance) {
		return false;
	}

	@Override
	public void save(Worker instance) {

	}
}
