package ru.ifmo.worker.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ifmo.util.query.Group;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@RestController("/api/extra")
@RequiredArgsConstructor
public class ExtraController {
	private final WorkerService service;

	@GetMapping("/grouped")
	private List<Group> handleGrouped() {
		return service.countGrouped();
	}

	@GetMapping("/unique")
	private EnumSet<Worker.Status> handleUnique() {
		return service.findDistinctStatuses();
	}

	@GetMapping("/named")
	private Collection<Worker> handleNamed(@RequestParam("like") String string) {
		return service.findNamedLike(string);
	}
}