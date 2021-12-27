package ru.ifmo.worker.repo.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.util.query.Group;
import ru.ifmo.worker.api.Column;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GroupRowMapper implements RowMapper<Group> {
	@Override
	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Group.builder()
		            .x(rs.getInt(Column.X.getName()))
		            .y(rs.getInt(Column.Y.getName()))
		            .count(rs.getInt("count"))
		            .build();
	}
}