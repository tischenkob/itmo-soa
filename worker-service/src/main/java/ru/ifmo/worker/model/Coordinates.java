package ru.ifmo.worker.model;

import javax.persistence.Embeddable;

@Embeddable
public class Coordinates {
    private float x; //Максимальное 48
    private int y; //Максимальное 676
}
