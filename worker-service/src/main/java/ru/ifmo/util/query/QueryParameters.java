package ru.ifmo.util.query;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class QueryParameters {
	private final Set<Filter> filters = new HashSet<>();
	private final Set<Sort> sorts = new HashSet<>();
	Page page;

	public void add(Filter value) {
		filters.add(value);
	}

	public void add(Sort value) {
		sorts.add(value);
	}

	public void set(Page value) {
		if (page != null) {
			throw new IllegalStateException("There cannot be more than one limit");
		}
		page = value;
	}

	public boolean isEmpty() {
		return filters.isEmpty() && sorts.isEmpty() && page == null;
	}
}
