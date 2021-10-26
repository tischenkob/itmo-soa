package ru.ifmo.worker.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "people")
public class Person {
    @Id
    @NotEmpty
    @NotNull
    private String passportID; //не может быть пустой, не null
    @NotNull
    @Enumerated(EnumType.STRING)
    private EyeColor eyeColor; //не null
    @NotNull
    @Enumerated(EnumType.STRING)
    private HairColor hairColor; //не null
    @NotNull
    @Enumerated(EnumType.STRING)
    private Country nationality; //не null

    enum EyeColor {
        GREEN,
        YELLOW,
        BROWN
    }

    enum HairColor {
        RED,
        BLACK,
        BLUE,
        WHITE,
        BROWN
    }
}
