package ru.ifmo.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static java.util.Objects.requireNonNull;

@Getter
@Setter
@Builder
@ToString
public class Person {
	private String passport; //не может быть пустой, не null
	private EyeColor eyeColor; //не null
	private HairColor hairColor; //не null
	private Country nationality; //не null

	public Person(String passport, EyeColor eyeColor, HairColor hairColor, Country nationality) {
		if (passport == null || passport.trim().isEmpty()) {
			throw new IllegalArgumentException("Passport cannot be empty or null");
		}
		this.passport = passport;
		this.eyeColor = requireNonNull(eyeColor, "eyeColor cannot be null");
		this.hairColor = requireNonNull(hairColor, "hairColor cannot be null");
		this.nationality = requireNonNull(nationality, "nationality cannot be null");
	}

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
