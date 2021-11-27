package ru.ifmo.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class Requests {

	public static int idParsedFrom(HttpServletRequest request) {
		String path = request.getPathInfo();
		if (path == null) {
			throw new IllegalArgumentException("Path must contain and id.");
		}
		String idString = stripRoot(path);
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

	public static String stripRoot(String path) {
		return path.substring(1);
	}

	public static boolean containsId(HttpServletRequest request) {
		try {
			final String path = request.getPathInfo();
			return path != null && parseInt(stripRoot(path)) > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String bodyOf(HttpServletRequest request) throws IOException {
		StringBuilder body = new StringBuilder();
		try (BufferedReader in = request.getReader()) {
			String line = in.readLine();
			while (line != null) {
				body.append(line);
				line = in.readLine();
			}
		}
		return body.toString();
	}

}
