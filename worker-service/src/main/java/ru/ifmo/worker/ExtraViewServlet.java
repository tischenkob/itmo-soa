package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import ru.ifmo.util.query.Group;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@RequiredArgsConstructor
public class ExtraViewServlet extends HttpServlet {
	private final WorkerService service;
	private final ITemplateEngine templateEngine;
	private final Supplier<NoSuchElementException> workerNotFoundException = () -> new NoSuchElementException("Worker not found.");
	private final String viewName = "extra";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Group> groups = service.countGrouped();
		EnumSet<Worker.Status> statuses = service.findDistinctStatuses();
		String substring = request.getParameter("like");
		Collection<Worker> foundWorkers = service.findNamedLike(substring);

		// Show page
		Context viewContext = new Context();
		viewContext.setVariable("workers", foundWorkers);
		viewContext.setVariable("statuses", statuses);
		viewContext.setVariable("groups", groups);
		templateEngine.process(viewName, viewContext, response.getWriter());
		response.setStatus(SC_OK);
	}

	public void destroy() {
	}
}
