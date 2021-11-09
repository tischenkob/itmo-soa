package ru.ifmo.util;

public interface XmlConverter {
	<T> String toXml(T object);
	<T> T fromXml(String xml, Class<T> clazz);
}
