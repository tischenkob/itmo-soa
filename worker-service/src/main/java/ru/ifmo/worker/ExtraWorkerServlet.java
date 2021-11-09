package ru.ifmo.worker;

import lombok.RequiredArgsConstructor;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class ExtraWorkerServlet extends HttpServlet {
	private final WorkerService service;
	private final XmlConverter xmlConverter;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

	}
}
