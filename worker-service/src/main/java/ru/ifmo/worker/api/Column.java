package ru.ifmo.worker.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum Column {
	ID("id"),
	NAME("name"),
	X("x"),
	Y("y"),
	CREATED("created"),
	SALARY("salary"),
	HIRED("hired"),
	QUIT("quit"),
	STATUS("status"),
	PASSPORT("passport"),
	EYE_COLOR("eye_color"),
	HAIR_COLOR("hair_color"),
	NATIONALITY("nationality");

	@Getter
	private final String name;
}
