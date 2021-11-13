package ru.ifmo;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

import static javax.servlet.RequestDispatcher.*;
import static javax.servlet.http.HttpServletResponse.*;

@WebServlet("/error")
public class ErrorHandler extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Exception exception = (Exception) request.getAttribute(ERROR_EXCEPTION);
		String message = (String) request.getAttribute(ERROR_MESSAGE);
		String servletName = (String) request.getAttribute(ERROR_SERVLET_NAME);
		System.out.printf("ERROR: Exception '%s' from servlet '%s'", message, servletName);

		if (exception instanceof IllegalArgumentException) {
			response.sendError(SC_BAD_REQUEST, message);
		}

		if (exception instanceof NoSuchElementException) {
			response.sendError(SC_NOT_FOUND, message);
		}
	}
}
