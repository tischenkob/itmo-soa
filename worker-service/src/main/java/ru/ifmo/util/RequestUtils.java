package ru.ifmo.util;

import lombok.var;
import ru.ifmo.worker.api.ParameterExtractor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class RequestUtils {

	public static int idParsedFrom(HttpServletRequest request) {
		String path = request.getPathInfo();
		if (path == null) {
			throw new IllegalArgumentException("Path must contain and id.");
		}
		String idString = path.substring(1);
		try {
			int id = parseInt(idString);
			if (id <= 0) {
				throw new IllegalArgumentException("Id must be greater than 0.");
			}
			return id;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Id must be a number.");
		}
	}

	public static boolean containsId(HttpServletRequest request) {
		try {
			final String path = request.getPathInfo();
			return path != null && parseInt(path.substring(1)) > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String bodyOf(HttpServletRequest request) throws IOException {
		StringBuilder body = new StringBuilder();
		try (var in = request.getReader()) {
			String line = in.readLine();
			while (line != null) {
				body.append(line);
				line = in.readLine();
			}
		}
		return body.toString();
	}

}
