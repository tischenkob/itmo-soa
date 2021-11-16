package ru.ifmo.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Containers {

	@SafeVarargs
	public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... pairs) {
		Map<K, V> map = new HashMap<>();
		for (Map.Entry<K, V> pair : pairs) {
			map.put(pair.getKey(), pair.getValue());
		}
		return map;
	}

	public static <K, V> Map.Entry<K, V> pair(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}

}
