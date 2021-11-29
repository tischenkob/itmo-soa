package ru.ifmo.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

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


	public Worker(int id, String name, Coordinates coordinates, LocalDateTime created, long salary, LocalDateTime hired, LocalDateTime quit, Status status, Person person) {
		if (salary <= 0) {
			throw new IllegalArgumentException("Salary must be greater than 0");
		}

		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty");
		}

		this.id = id;
		this.name = requireNonNull(name, "name cannot be null");
		this.coordinates = requireNonNull(coordinates, "coordinates cannot be null");
		this.created = created;
		this.salary = salary;
		this.hired = requireNonNull(hired, "hired cannot be null");
		this.quit = quit;
		this.status = requireNonNull(status, "status cannot be null");
		this.person = requireNonNull(person, "person cannot be null");
	}

	public enum Status {
		FIRED,
		HIRED,
		RECOMMENDED_FOR_PROMOTION,
		REGULAR,
		PROBATION
	}
}
