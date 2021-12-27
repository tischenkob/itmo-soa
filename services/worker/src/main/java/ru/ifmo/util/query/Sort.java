package ru.ifmo.util.query;

import lombok.Value;

@Value(staticConstructor = "of")
public class Sort {
	String field;
	Order order;

	public enum Order {
		ASCENDING, DESCENDING;

		public static Order of(String value) {
			if ("asc".equals(value)) return ASCENDING;
			if ("dsc".equals(value)) return DESCENDING;
			throw new IllegalArgumentException("Illegal argument for Order. Allowed are 'asc', 'dsc'");
		}
	}
}
