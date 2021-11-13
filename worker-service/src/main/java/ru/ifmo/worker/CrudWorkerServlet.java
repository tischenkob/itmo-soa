package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import lombok.var;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.worker.api.ParameterExtractor;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.ifmo.util.RequestUtils.*;

@RequiredArgsConstructor
public class CrudWorkerServlet extends HttpServlet {
	private final WorkerService service;
	private final XmlConverter converter;
	private final Supplier<NoSuchElementException> workerNotFoundException = () -> new NoSuchElementException("Worker not found.");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Object responseContent = containsId(request)
		                         ? service.findBy(idParsedFrom(request))
		                                  .orElseThrow(workerNotFoundException)
		                         : service.findWith(ParameterExtractor.parametersFrom(request));

		try (var out = response.getWriter()) {
			out.println(converter.toXml(responseContent));
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		service.deleteBy(idParsedFrom(req));
		resp.setStatus(SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		service.save(workerFromBodyOf(req));
		resp.setStatus(SC_CREATED);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Worker worker = workerFromBodyOf(req);
		worker.setId(idParsedFrom(req));
		service.update(worker);
		resp.setStatus(SC_OK);
	}

	private Worker workerFromBodyOf(HttpServletRequest req) throws IOException {
		return converter.fromXml(bodyOf(req), Worker.class);
	}

	public void destroy() {
	}
}