package ru.ifmo.worker.api;

import ru.ifmo.util.query.Filter;
import ru.ifmo.util.query.Page;
import ru.ifmo.util.query.QueryParameters;
import ru.ifmo.util.query.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class ParameterExtractor {

	private static final List<String> COLUMNS = Arrays.stream(Column.values())
	                                                  .map(Column::getName)
	                                                  .collect(toList());
	private static final String LIMIT_PARAMETER = "limit";
	private static final String SORT_PARAMETER = "sort";
	private static final Set<String> ALLOWED_PARAMETERS = new HashSet<>();
	private static final char VALUE_DELIMITER = ':';

	static {
		ALLOWED_PARAMETERS.addAll(COLUMNS);
		ALLOWED_PARAMETERS.add(LIMIT_PARAMETER);
		ALLOWED_PARAMETERS.add(SORT_PARAMETER);
	}

	public static QueryParameters parametersFrom(HttpServletRequest request) {
		QueryParameters parameters = new QueryParameters();
		request.getParameterMap().forEach((parameter, values) -> {
			validateParameter(parameter);
			for (String parameterValue : values) {
				validateParameterValue(parameter, parameterValue);
				parseQueryParameterAndCollect(parameter, parameterValue, parameters);
			}
		});
		return parameters;
	}

	private static void validateParameter(String parameter) {
		if (!ALLOWED_PARAMETERS.contains(parameter)) {
			throw new IllegalArgumentException("Illegal parameter: " + parameter);
		}
	}

	private static void validateParameterValue(String parameter, String parameterValue) {
		if (parameterValue.indexOf(':') == -1) {
			throw new IllegalArgumentException("Parameter value must contain '" + VALUE_DELIMITER + "' delimiter. >>" + parameter);
		}
	}

	private static void parseQueryParameterAndCollect(String parameter, String parameterValue, QueryParameters parameters) {
		final int delimiterIndex = parameterValue.indexOf(VALUE_DELIMITER);
		String option = parameterValue.substring(0, delimiterIndex);
		String value = parameterValue.substring(delimiterIndex + 1);
		switch (parameter) {
			case LIMIT_PARAMETER:
				try {
					parameters.set(Page.of(parseInt(option), parseInt(value)));
					return;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Limit values must be numbers");
				}
			case SORT_PARAMETER:
				parameters.add(Sort.of(value, Sort.Order.of(option)));
				return;
			default:
				boolean usesQuotes = Column.valueOf(parameter.toUpperCase()).isQuoted();
				parameters.add(Filter.of(parameter, option, value, usesQuotes));
		}
	}

}
