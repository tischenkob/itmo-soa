package ru.ifmo.hr.view;

import ru.ifmo.hr.Main;
import ru.ifmo.hr.worker.Workers;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/dashboard")
public class HrDashboard {
    private static final String templateName = "dashboard.html";
    Workers workers;

    @PostConstruct
    void init() {
        workers = Main.getWorkers();
    }

    @GET
    public Response dashboardView() {
        HtmlResponse response = HtmlResponse.of(templateName, workers.findAll());
        return Response.ok().entity(response).build();
    }

}