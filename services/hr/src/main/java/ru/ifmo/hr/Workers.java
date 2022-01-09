package ru.ifmo.hr;

import ru.ifmo.worker.model.Worker;

import javax.ws.rs.*;

@Path("/workers")
public interface Workers {

    @GET
    @Path("/{id}")
    @Consumes("application/xml")
    Worker findBy(@PathParam("id") int id);

    @PUT
    @Path("/{id}")
    @Produces("application/xml")
    void update(@PathParam("id") int id, Worker worker);

}