package ru.ifmo.worker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class Coordinates {
	private float x; //Максимальное 48
	private int y; //Максимальное 676
}
