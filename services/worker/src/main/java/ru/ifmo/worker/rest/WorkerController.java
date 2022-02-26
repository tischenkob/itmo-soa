package ru.ifmo.worker.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.NoSuchElementException;

import static ru.ifmo.worker.api.ParameterExtractor.parametersFrom;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Worker worker) {
        service.save(worker);
    }

    @GetMapping
    public Collection<Worker> readAll(HttpServletRequest request) {
        return service.findWith(parametersFrom(request));
    }

    @GetMapping("/{id}")
    public Worker read(@PathVariable int id) {
        return service.findBy(id).orElseThrow(WorkerController::workerNotFoundException);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody Worker worker) {
        worker.setId(id);
        service.update(worker);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.deleteBy(id);
    }

    private static NoSuchElementException workerNotFoundException() {
        return new NoSuchElementException("Worker not found.");
    }
}