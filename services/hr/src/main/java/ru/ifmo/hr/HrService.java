package ru.ifmo.hr;

import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import ru.ifmo.worker.model.Organisation;
import ru.ifmo.worker.model.Worker;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

@Path("/api")
public class HrService {

    @Context
    ServletContext context;

    private Workers workers;

    @SneakyThrows
    @PostConstruct
    private void initialise() {
        System.out.println("Context: " + context);
        URI apiUri = new URI(getProperties().getProperty("services.worker.uri"));
        workers = RestClientBuilder.newBuilder()
            .baseUri(apiUri)
            .build(Workers.class);
    }

    private Properties getProperties() throws IOException {
        try (InputStream stream = context.getResourceAsStream("WEB-INF/service.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }

    @GET
    @Path("/ping")
    public Response healthcheck() {
        return Response.ok("Hello!").build();
    }

    @POST
    @Path("/move/{id}/{from}/{to}")
    public Response moveBetweenOrgs(@PathParam("id") int id, @PathParam("from") int from, @PathParam("to") int to) {
        Worker worker = workers.findBy(id);
        if (from != worker.getOrganisation().getId()) {
            throw new IllegalArgumentException("`from id` is different from the actual value");
        }
        worker.setOrganisation(Organisation.of(to, worker.getOrganisation().getName()));
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