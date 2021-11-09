package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import lombok.var;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CrudWorkerServlet extends HttpServlet {
	private final WorkerService service;
	private final XmlConverter xmlConverter;
	private final Supplier<NoSuchElementException> workerNotFoundException = () -> new NoSuchElementException("No worker with this id.");

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getPathInfo();
		if (path == null || path.trim().isEmpty()) {
			processCollectionRequest(request, response);
			return;
		}

		int id = extractId(path);
		Worker worker = service.findBy(id).orElseThrow(workerNotFoundException);

		try (var out = response.getWriter()) {
			String xml = xmlConverter.toXml(worker);
			out.println(xml);
		}
	}

	private int extractId(String path) {
		String idString = path.substring(path.indexOf("/") + 1);
		try {
			int id = Integer.parseInt(idString);
			if (id <= 0) {
				throw new IllegalArgumentException("Id should be greater than 0.");
			}
			return id;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Id is not a number.");
		}
	}

	private void processCollectionRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Collection<Worker> workers = service.findAll();
		//Collection<Worker> workers = service.findWith(request.getParameterMap());
		String xml = xmlConverter.toXml(workers);
		response.setStatus(200);
		try (var out = response.getWriter()) {
			out.println(xml);
		}
	}

	public void destroy() {
	}
}