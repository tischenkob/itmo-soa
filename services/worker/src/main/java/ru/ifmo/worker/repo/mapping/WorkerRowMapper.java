package ru.ifmo.worker.repo.mapping;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.worker.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static ru.ifmo.util.Nulls.map;

@Component
public class WorkerRowMapper implements RowMapper<Worker> {
    @Override
    public Worker mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Worker.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .coordinates(Coordinates.of(rs.getFloat("x"),
                                        rs.getInt("y")))
            .created(rs.getTimestamp("created").toLocalDateTime())
            .salary(rs.getLong("salary"))
            .hired(rs.getTimestamp("hired").toLocalDateTime())
            .quit(map(rs.getTimestamp("quit"), Timestamp::toLocalDateTime))
            .status(Worker.Status.valueOf(rs.getString("status")))
            .person(Person.builder()
                        .passport(rs.getString("passport"))
                        .eyeColor(Person.EyeColor.valueOf(rs.getString("eye_color")))
                        .hairColor(Person.HairColor.valueOf(rs.getString("hair_color")))
                        .nationality(Country.valueOf(rs.getString("nationality")))
                        .build())
            .organisation(Organisation.of(rs.getInt("o.id"),
                                          rs.getString("o.name")))
            .build();
    }
}