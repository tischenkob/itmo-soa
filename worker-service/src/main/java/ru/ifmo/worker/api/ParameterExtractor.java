package ru.ifmo.worker.api;

import lombok.var;
import ru.ifmo.util.query.Filter;
import ru.ifmo.util.query.Page;
import ru.ifmo.util.query.QueryParameter;
import ru.ifmo.util.query.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.lang.Integer.parseInt;
import static ru.ifmo.util.Containers.*;

public class ParameterExtractor {

	private static final List<String> COLUMNS = Arrays.asList(map(Column.values(), Column::toString));
	private static final String LIMIT_PARAMETER = "limit";
	private static final String SORT_PARAMETER = "sort";
	private static final Set<String> ALLOWED_PARAMETERS = new HashSet<>();
	private static final char VALUE_DELIMITER = ':';

	static {
		ALLOWED_PARAMETERS.addAll(COLUMNS);
		ALLOWED_PARAMETERS.add(LIMIT_PARAMETER);
		ALLOWED_PARAMETERS.add(SORT_PARAMETER);
	}

	public static Collection<QueryParameter> parametersFrom(HttpServletRequest request) {
		Set<QueryParameter> parameters = new HashSet<>();
		for (var entry : request.getParameterMap().entrySet()) {
			String parameter = entry.getKey();

			if (!ALLOWED_PARAMETERS.contains(parameter)) {
				throw new IllegalArgumentException("Illegal parameter: " + parameter);
			}

			for (String parameterValue : entry.getValue()) {
				if (parameterValue.indexOf(':') == -1) {
					throw new IllegalArgumentException("Parameter value must contain ':' delimiter. >>" + parameter);
				}

				final int delimiterIndex = parameterValue.indexOf(VALUE_DELIMITER);
				String option = parameterValue.substring(0, delimiterIndex);
				String value = parameterValue.substring(delimiterIndex + 1);
				parameters.add(queryParameterFor(parameter, option, value));
			}
		}
		return Collections.unmodifiableCollection(parameters);
	}

	private static QueryParameter queryParameterFor(String parameter, String option, String value) {
		switch(parameter) {
			case LIMIT_PARAMETER:
				try {
					return Page.of(parseInt(option), parseInt(value));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Limit values must be numbers");
				}
			case SORT_PARAMETER:
				return Sort.of(option, Sort.Order.of(value));
			default:
				return Filter.of(option, value);
		}
	}

}
