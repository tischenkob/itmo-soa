package ru.ifmo.hr.resources;

import jdk.internal.joptsimple.internal.Strings;
import ru.ifmo.hr.Main;
import ru.ifmo.hr.worker.Workers;
import ru.ifmo.worker.model.Organisation;
import ru.ifmo.worker.model.Worker;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/api")
public class HrService {

    private Workers workers;

    @PostConstruct
    void init() {
        workers = Main.getWorkers();
    }

    @POST
    @Path("/move/{id}/{from}/{to}")
    public Response moveBetweenOrgs(@PathParam("id") int id, @PathParam("from") int from, @PathParam("to") int to) {
        if (id <= 0 || from <= 0 || to <= 0) {
            throw new IllegalArgumentException("All arguments must be > 0");
        }
        Response workerResponse = workers.findBy(id);
        if (!workerResponse.hasEntity()) {
            return workerResponse;
        }
        Worker worker = workerResponse.readEntity(Worker.class);
        if (from != worker.getOrganisation().getId()) {
            throw new IllegalArgumentException("`from id` is different from the actual value");
        }
        worker.setOrganisation(Organisation.of(to, Strings.EMPTY));
        return workers.update(id, worker);
    }

    @POST
    @Path("index/{id}/{coeff}")
    public Response indexSalary(@PathParam("id") int id, @PathParam("coeff") float coeff) {
        if (coeff <= 0) {
            throw new IllegalArgumentException("`coeff` cannot be <= 0");
        }
        Response workerResponse = workers.findBy(id);
        if (!workerResponse.hasEntity()) {
            return workerResponse;
        }
        Worker worker = workerResponse.readEntity(Worker.class);
        long newSalary = (long) (worker.getSalary() * (1 + coeff));
        worker.setSalary(newSalary);
        return workers.update(id, worker);
    }
}