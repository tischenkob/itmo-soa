package ru.ifmo.hr.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public class HealthCheck {
    @GET
    public Response healthcheck() {
        return Response.ok("OK!").build();
    }
}