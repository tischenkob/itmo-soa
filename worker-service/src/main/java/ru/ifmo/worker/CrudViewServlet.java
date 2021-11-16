package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;
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
import java.util.*;
import java.util.function.Supplier;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static ru.ifmo.util.Nulls.map;
import static ru.ifmo.util.RequestUtils.containsId;
import static ru.ifmo.util.RequestUtils.idParsedFrom;
import static ru.ifmo.worker.api.ParameterExtractor.parametersFrom;

@RequiredArgsConstructor
public class CrudViewServlet extends HttpServlet {
	private final WorkerService service;
	private final ITemplateEngine templateEngine;
	private final Supplier<NoSuchElementException> workerNotFoundException = () -> new NoSuchElementException("Worker not found.");

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
		templateEngine.process("crud", viewContext, response.getWriter());
		response.setStatus(SC_OK);
	}

	@Override
	@SneakyThrows
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		service.save(workerFromBodyOf(req));
		resp.setStatus(SC_CREATED);
		resp.sendRedirect("/crud");
	}

	@Override
	@SneakyThrows
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		Worker worker = workerFromBodyOf(req);
		worker.setId(idParsedFrom(req));
		service.update(worker);
		resp.setStatus(SC_OK);
		resp.sendRedirect("/crud");
	}

	@Override
	@SneakyThrows
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		service.deleteBy(idParsedFrom(req));
		resp.setStatus(SC_OK);
		resp.sendRedirect("/crud");
	}

	@SneakyThrows
	private Worker workerFromBodyOf(HttpServletRequest req) {

		Map<String, String> parameters = new HashMap<>();
		try (var reader = req.getReader()) {
			String line = reader.readLine();
			while (line != null) {
				if (line.indexOf(':') == line.length()) {
					parameters.put(line.substring(0, line.indexOf(':')), null);
				} else {
					parameters.put(line.substring(0, line.indexOf(':')), line.substring(line.indexOf(':') + 1));
				}
				line = reader.readLine();
			}
		}

		final Person person = Person.builder()
		                            .passport(requireString(parameters, "passport"))
		                            .eyeColor(Person.EyeColor.valueOf(requireString(parameters, "eye-color")))
		                            .hairColor(Person.HairColor.valueOf(requireString(parameters, "hair-color")))
		                            .nationality(Country.valueOf(requireString(parameters, "nationality")))
		                            .build();
		return Worker.builder()
		             .name(requireString(parameters, "name"))
		             .coordinates(Coordinates.of((float) requireNumber(parameters, "x"),
		                                         (int) requireNumber(parameters, "y")))
		             .created(requireDate(parameters, "created"))
		             .salary((int) requireNumber(parameters, "salary"))
		             .hired(requireDate(parameters, "hired"))
		             .quit(map(parameters.get("quit"), LocalDateTime::parse))
		             .status(Worker.Status.valueOf(requireString(parameters, "status")))
		             .person(person)
		             .build();
	}

	private String requireString(Map<String, String> parameters, String parameter) {
		String value = parameters.get(parameter);
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(parameter + " is a required parameter");
		}
		return value;
	}

	private double requireNumber(Map<String, String> parameters, String parameter) {
		String value = requireString(parameters, parameter);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(parameter + " must be a valid number");
		}
	}

	private LocalDateTime requireDate(Map<String, String> parameters, String parameter) {
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
