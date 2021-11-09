package ru.ifmo;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.RequestDispatcher.*;

@Slf4j
@WebServlet("/error")
public class ErrorHandler extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Exception exception = (Exception) request.getAttribute(ERROR_EXCEPTION);
		String message = (String) request.getAttribute(ERROR_MESSAGE);
		String servletName = (String) request.getAttribute(ERROR_SERVLET_NAME);
		log.info("Exception {} from servlet {}", message, servletName);

		if (exception instanceof IllegalArgumentException) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
		}
	}
}
