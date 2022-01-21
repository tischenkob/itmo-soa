package ru.ifmo.hr.worker;

import ru.ifmo.worker.model.Worker;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/workers")
@Singleton
public interface Workers {

    @GET
    @Path("/{id}")
    @Consumes("application/xml")
    Response findBy(@PathParam("id") int id);

    @GET
    @Consumes("application/xml")
    Collection<Worker> findAll();

    @PUT
    @Path("/{id}")
    @Produces("application/xml")
    Response update(@PathParam("id") int id, Worker worker);

}