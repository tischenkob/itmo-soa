package ru.ifmo.hr.worker;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ru.ifmo.worker.model.Worker;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/workers")
@Singleton
@RegisterRestClient
public interface Workers {

    @GET
    @Path("/{id}")
    @Consumes(APPLICATION_XML)
    Response findBy(@PathParam("id") int id);

    @GET
    @Consumes(APPLICATION_XML)
    Collection<Worker> findAll();

    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_XML)
    Response update(@PathParam("id") int id, Worker worker);

}