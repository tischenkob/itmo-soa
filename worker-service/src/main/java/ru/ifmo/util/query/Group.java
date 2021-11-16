package ru.ifmo.util.query;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class Group {
	int x;
	int y;
	int count;
}
