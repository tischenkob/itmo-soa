package ru.ifmo.worker.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue
    private int id; //>0,уникальным, генерироваться автоматически
    @NotNull
    @NotEmpty
    private String name; //не может быть null, пустой
    @Embedded
    @NotNull
    private Coordinates coordinates; //не null
    @NotNull
    private LocalDateTime creationDate; //не null, должно генерироваться
    private long salary; //больше 0
    @NotNull
    private LocalDateTime startDate; //не null
    private LocalDateTime endDate; //может быть null
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status; //не null
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person; //не null

    enum Status {
        FIRED,
        HIRED,
        RECOMMENDED_FOR_PROMOTION,
        REGULAR,
        PROBATION
    }
}

