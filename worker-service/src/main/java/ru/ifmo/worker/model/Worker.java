package ru.ifmo.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class Worker {
	private int id; //>0,уникальным, генерироваться автоматически
	private String name; //не может быть null, пустой
	private Coordinates coordinates; //не null
	private LocalDateTime created; //не null, должно генерироваться
	private long salary; //больше 0
	private LocalDateTime hired; //не null
	private LocalDateTime quit; //может быть null
	private Status status; //не null
	private Person person; //не null

	public enum Status {
		FIRED,
		HIRED,
		RECOMMENDED_FOR_PROMOTION,
		REGULAR,
		PROBATION
	}
}

