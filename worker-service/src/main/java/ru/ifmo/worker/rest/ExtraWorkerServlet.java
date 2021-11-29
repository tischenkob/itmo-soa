package ru.ifmo.worker.rest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.util.query.Group;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static ru.ifmo.util.Containers.mapOf;
import static ru.ifmo.util.Containers.pairOf;

@RequiredArgsConstructor
public class ExtraWorkerServlet extends HttpServlet {
	private final WorkerService service;
	private final XmlConverter converter;

	Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>>
			handlerMapping = mapOf(
			pairOf("grouped", this::handleGrouped),
			pairOf("unique", this::handleUnique),
			pairOf("named", this::handleNamed));

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String operation = stripBasePath(request.getServletPath());
		handlerMapping.get(operation).accept(request, response);
	}

	private static String stripBasePath(String servletPath) {
		return servletPath.substring(servletPath.lastIndexOf('/') + 1);
	}

	@SneakyThrows
	private void handleGrouped(HttpServletRequest request, HttpServletResponse response) {
		List<Group> groups = service.countGrouped();
		try (PrintWriter writer = response.getWriter()) {
			writer.print(converter.toXml(groups));
		}
		response.setStatus(SC_OK);
	}

	@SneakyThrows
	private void handleUnique(HttpServletRequest request, HttpServletResponse response) {
		EnumSet<Worker.Status> statuses = service.findDistinctStatuses();
		try (PrintWriter writer = response.getWriter()) {
			writer.print(converter.toXml(statuses));
		}
		response.setStatus(SC_OK);
	}

	@SneakyThrows
	private void handleNamed(HttpServletRequest request, HttpServletResponse response) {
		String substring = request.getParameter("like");
		Collection<Worker> foundWorkers = service.findNamedLike(substring);
		try (PrintWriter writer = response.getWriter()) {
			writer.print(converter.toXml(foundWorkers));
		}
		response.setStatus(SC_OK);
	}
}
