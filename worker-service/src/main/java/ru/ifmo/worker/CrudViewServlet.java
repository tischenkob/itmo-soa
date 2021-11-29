package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import ru.ifmo.worker.model.Coordinates;
import ru.ifmo.worker.model.Country;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static ru.ifmo.util.Requests.containsId;
import static ru.ifmo.util.Requests.idParsedFrom;
import static ru.ifmo.worker.api.ParameterExtractor.parametersFrom;

@RequiredArgsConstructor
public class CrudViewServlet extends HttpServlet {
	private final WorkerService service;
	private final ITemplateEngine templateEngine;
	private final Supplier<NoSuchElementException> workerNotFoundException = () -> new NoSuchElementException("Worker not found.");
	private final String viewName = "index";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Collection<Worker> workers;

		if (containsId(request)) {
			Worker foundWorker = service.findBy(idParsedFrom(request))
			                            .orElseThrow(workerNotFoundException);
			workers = Collections.singletonList(foundWorker);
		} else {
			workers = service.findWith(parametersFrom(request));
		}
		Context viewContext = new Context();
		viewContext.setVariable("workers", workers);
		templateEngine.process(viewName, viewContext, response.getWriter());
		response.setStatus(SC_OK);
	}

	@Override
	@SneakyThrows
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		if ("put".equals(req.getParameter("_method"))) {
			doPut(req, resp);
			return;
		}
		service.save(workerFromBodyOf(req));
		resp.setStatus(SC_CREATED);
//		resp.sendRedirect(getLocation(req));
	}

	@Override
	@SneakyThrows
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		Worker worker = workerFromBodyOf(req);
		worker.setId(idParsedFrom(req));
		service.update(worker);
		resp.setStatus(SC_OK);
//		resp.sendRedirect(getLocation(req));
	}

	@Override
	@SneakyThrows
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		service.deleteBy(idParsedFrom(req));
		resp.setStatus(SC_OK);
//		resp.sendRedirect(getLocation(req));
	}

	private String getLocation(HttpServletRequest req) {
		return req.getContextPath() + "/" + viewName;
	}

	@SneakyThrows
	private Worker workerFromBodyOf(HttpServletRequest req) {

		final Person person = Person.builder()
		                            .passport(requireString(req, "passport"))
		                            .eyeColor(Person.EyeColor.valueOf(requireString(req, "eye-color")))
		                            .hairColor(Person.HairColor.valueOf(requireString(req, "hair-color")))
		                            .nationality(Country.valueOf(requireString(req, "nationality")))
		                            .build();
		return Worker.builder()
		             .name(requireString(req, "name"))
		             .coordinates(Coordinates.of((float) requireNumber(req, "x"),
		                                         (int) requireNumber(req, "y")))
		             .salary((int) requireNumber(req, "salary"))
		             .hired(requireDate(req, "hired"))
		             .quit(optionalDate(req, "quit"))
		             .status(Worker.Status.valueOf(requireString(req, "status")))
		             .person(person)
		             .build();
	}

	private LocalDateTime optionalDate(HttpServletRequest req, String parameter) {
		String object = req.getParameter(parameter);
		if (object == null) return null;
		try {
			return LocalDateTime.parse(object);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("quit must be a valid date or null");
		}
	}

	private String requireString(HttpServletRequest parameters, String parameter) {
		String value = parameters.getParameter(parameter);
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(parameter + " is a required parameter");
		}
		return value;
	}

	private double requireNumber(HttpServletRequest parameters, String parameter) {
		String value = requireString(parameters, parameter);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(parameter + " must be a valid number");
		}
	}

	private LocalDateTime requireDate(HttpServletRequest parameters, String parameter) {
		String value = requireString(parameters, parameter);
		try {
			return LocalDateTime.parse(value);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(parameter + " must be a valid date");
		}
	}

	public void destroy() {
	}
}
