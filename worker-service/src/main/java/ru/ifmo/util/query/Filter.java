package ru.ifmo.util.query;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value(staticConstructor = "of")
public class Filter implements QueryParameter {
	private final static Map<String, String> SYMBOLS = new HashMap<>();

	String operator;
	String value;

	static {
		SYMBOLS.put("lt", "<");
		SYMBOLS.put("lte", "<=");
		SYMBOLS.put("gt", ">");
		SYMBOLS.put("gte", ">=");
		SYMBOLS.put("eq", "=");
	}

	public Filter(String operator, String value) {
		this.operator = SYMBOLS.getOrDefault(operator, operator); //  operator может быть и ключом, и значением
		if (!SYMBOLS.containsValue(this.operator)) {
			throw new IllegalArgumentException("Illegal filtering option: " + operator);
		}
		this.value = value;
	}
}
