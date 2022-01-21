package ru.ifmo.hr.view;

import ru.ifmo.hr.worker.Workers;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/dashboard")
public class HrDashboard {

    @Inject Workers workers;

    @GET
    public HtmlResponse dashboardView() {
        return HtmlResponse.of("dashboard.html", workers.findAll());
    }

}