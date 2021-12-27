package ru.ifmo.worker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Getter
@Setter
@Builder
@ToString
@XmlRootElement(name = "worker")
@XmlAccessorType(XmlAccessType.FIELD)
public class Worker {
    private int id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime created;
    private long salary;
    private LocalDateTime hired;
    private LocalDateTime quit;
    private Status status;
    @XmlElement(name = "person")
    private Person person;
    @XmlElement(name = "org")
    private Organisation organisation;


    public Worker(int id, String name, Coordinates coordinates, LocalDateTime created,
                  long salary, LocalDateTime hired, LocalDateTime quit, Status status,
                  Person person, Organisation organisation) {
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
        this.organisation = requireNonNull(organisation, "organisation cannot be null");
    }

    public enum Status {
        FIRED,
        HIRED,
        RECOMMENDED_FOR_PROMOTION,
        REGULAR,
        PROBATION
    }
}