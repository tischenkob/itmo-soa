package ru.ifmo.hr;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/api/hr")
public class HrService {

    @POST
    @Path("/move/{id}/{from}/{to}")
    public Response moveBetweenOrgs(@PathParam("id") int id, @PathParam("from") int from, @PathParam("to") int to) {
        return Response.ok().build();
    }

    @POST
    @Path("index/{id}/{coeff}")
    public Response indexSalary(@PathParam("id") int id, @PathParam("coeff") float coeff) {
        return Response.ok().build();
    }

}