package ru.ifmo.worker.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class Organisation {
    int id;
    String name;
}