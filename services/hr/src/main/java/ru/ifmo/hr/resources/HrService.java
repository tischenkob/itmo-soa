package ru.ifmo.hr.resources;

import jdk.internal.joptsimple.internal.Strings;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import ru.ifmo.hr.worker.Workers;
import ru.ifmo.worker.model.Organisation;
import ru.ifmo.worker.model.Worker;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.Properties;

@Path("/api")
public class HrService {
    private static final String heliosPropertiesPath = "WEB-INF/service-helios.properties";
    private static final String defaultPropertiesPath = "WEB-INF/service.properties";
    private final String certificatePath;
    private final String password;
    private final String propertiesPath;
    @Context
    ServletContext context;

    private Workers workers;

    {
        certificatePath = System.getProperty("ssl.cert");
        password = System.getProperty("ssl.pass");
        propertiesPath = "helios".equals(System.getProperty("env.profile"))
                         ? heliosPropertiesPath : defaultPropertiesPath;
    }

    @SneakyThrows
    @PostConstruct
    private void initialise() {
        URI uri = getWorkersUri();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream certificate = new FileInputStream(certificatePath)) {
            trustStore.load(certificate, password.toCharArray());
        }
        try {
            workers = RestClientBuilder.newBuilder()
                .baseUri(uri)
                .trustStore(trustStore)
                .hostnameVerifier((i, gnore) -> true)
                .build(Workers.class);
        } catch (Exception e) {
            System.err.println("Couldn't build the rest client for \n" +
                               "uri: " + uri + "\n" +
                               "props path: " + propertiesPath);
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private URI getWorkersUri() throws URISyntaxException, IOException {
        try (InputStream stream = context.getResourceAsStream(propertiesPath)) {
            Properties properties = new Properties();
            properties.load(stream);
            return new URI(properties.getProperty("services.worker.uri"));
        }
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