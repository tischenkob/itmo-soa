package ru.ifmo.util.query;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value(staticConstructor = "of")
public class Filter {
	private final static Map<String, String> SYMBOLS = new HashMap<>();

	String field;
	String operator;
	String value;
	boolean usesQuotes;

	static {
		SYMBOLS.put("lt", "<");
		SYMBOLS.put("lte", "<=");
		SYMBOLS.put("gt", ">");
		SYMBOLS.put("gte", ">=");
		SYMBOLS.put("eq", "=");
	}

	public Filter(String field, String operator, String value, boolean usesQuotes) {
		this.field = field;
		this.operator = SYMBOLS.getOrDefault(operator, operator); //  operator может быть и ключом, и значением
		if (!SYMBOLS.containsValue(this.operator)) {
			throw new IllegalArgumentException("Illegal filtering option: " + operator);
		}
		this.value = value;
		this.usesQuotes = usesQuotes;
	}

	@Override
	public String toString() {
		String value = usesQuotes ? "'" + getValue() + "'" : getValue();
		return field + operator + value;
	}
}
