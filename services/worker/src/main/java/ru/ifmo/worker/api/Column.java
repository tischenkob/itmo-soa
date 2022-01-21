package ru.ifmo.worker.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Column {
	ID("id", false),
	NAME("name"),
    X("x", false),
    Y("y", false),
    CREATED("created"),
    SALARY("salary", false),
    HIRED("hired"),
    QUIT("quit"),
    STATUS("status"),
    PASSPORT("passport"),
    EYE_COLOR("eye_color"),
    HAIR_COLOR("hair_color"),
    NATIONALITY("nationality"),
    ORG_ID("org_id");

    @Getter
    private final String name;
    @Getter
    private final boolean quoted;

    Column(String name) {
        this(name, true);
    }
}