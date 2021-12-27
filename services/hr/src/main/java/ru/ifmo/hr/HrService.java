package ru.ifmo.hr;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import ru.ifmo.worker.model.Organisation;
import ru.ifmo.worker.model.Worker;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

@Path("/api/hr")
public class HrService {

    @Context
    ServletContext context;

    @Resource
    private Workers workers;

    @SneakyThrows
    @PostConstruct
    private void initialise() {
        try (Scanner input = new Scanner(getUrlStream())) {
            URI apiUri = new URI(input.nextLine());
            workers = RestClientBuilder.newBuilder()
                .baseUri(apiUri)
                .build(Workers.class);
        }
    }

    private InputStream getUrlStream() {
        return context.getResourceAsStream("services.worker.uri");
    }

    @POST
    @Path("/move/{id}/{from}/{to}")
    public Response moveBetweenOrgs(@PathParam("id") int id, @PathParam("from") int from, @PathParam("to") int to) {
        Worker worker = workers.findBy(id);
        if (from != worker.getOrganisation().getId()) {
            throw new IllegalArgumentException("`from id` is different from the actual value");
        }
        worker.setOrganisation(Organisation.of(to, null));
        workers.update(id, worker);
        return Response.ok().build();
    }

    @POST
    @Path("index/{id}/{coeff}")
    public Response indexSalary(@PathParam("id") int id, @PathParam("coeff") float coeff) {
        if (coeff <= 0) {
            throw new IllegalArgumentException("`coeff` cannot be <= 0");
        }
        Worker worker = workers.findBy(id);
        long salary = worker.getSalary();
        worker.setSalary((long) (salary * (1 + coeff)));
        workers.update(id, worker);
        return Response.ok().build();
    }

}