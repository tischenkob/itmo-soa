package ru.ifmo.util.query;

import lombok.Value;

@Value(staticConstructor = "of")
public class Page {
	int limit;
	int offset;

	Page(int limit, int offset) {
		if (limit < 0 || offset < 0) {
			throw new IllegalArgumentException("Limit values must be >= 0");
		}
		this.limit = limit;
		this.offset = offset;
	}
}
