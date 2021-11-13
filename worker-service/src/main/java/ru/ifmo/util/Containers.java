package ru.ifmo.util;

import java.util.function.Function;

public class Containers {

	public static boolean arrayContains(Object[] values, Object value) {
		for (Object o : values) {
			if (o.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static <From, To> To[] map(From[] array, Function<From, To> mapper) {
		Object[] result = new Object[array.length];
		int i = 0;
		for (From from : array) {
			result[i++] = mapper.apply(from);
		}
		return (To[]) result;
	}
}
