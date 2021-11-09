package ru.ifmo.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Person {
	private String passport; //не может быть пустой, не null
	private EyeColor eyeColor; //не null
	private HairColor hairColor; //не null
	private Country nationality; //не null

	public enum EyeColor {
		GREEN,
		YELLOW,
		BROWN
	}

	public enum HairColor {
		RED,
		BLACK,
		BLUE,
		WHITE,
		BROWN
	}
}
