package ru.ifmo.worker.model;

import lombok.*;

import static java.util.Objects.requireNonNull;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Person {
	private String passport;
	private EyeColor eyeColor;
	private HairColor hairColor;
	private Country nationality;

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