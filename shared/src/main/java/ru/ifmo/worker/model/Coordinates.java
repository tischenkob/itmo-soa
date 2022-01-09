package ru.ifmo.worker.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Coordinates {
	private float x; //Максимальное 48
	private int y; //Максимальное 676

	private Coordinates(float x, int y) {
		setX(x);
		setY(y);
	}

	public void setX(float x) {
		if (x > 48) {
			throw new IllegalArgumentException("x must be <= 48");
		}
		this.x = x;
	}

	public void setY(int y) {
		if (x > 676) {
			throw new IllegalArgumentException("y must be <= 676");
		}
		this.y = y;
	}

	public static Coordinates of(float x, int y) {
		return new Coordinates(x, y);
	}
}