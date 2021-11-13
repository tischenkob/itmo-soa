package ru.ifmo.util.query;

import lombok.Value;

@Value(staticConstructor = "of")
public class Page implements QueryParameter {
	int limit;
	int offset;
}
