package ru.ifmo.worker.repo;

import ca.krasnay.sqlbuilder.DeleteBuilder;
import ca.krasnay.sqlbuilder.InsertBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.ifmo.worker.model.Coordinates;
import ru.ifmo.worker.model.Country;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class DefaultWorkerRepository implements WorkerRepository {

	private final JdbcTemplate jdbc;
	private final String PEOPLE_TABLE = "soa_people";
	private final String WORKERS_TABLE = "soa_workers";
	private final RowMapper<Worker> ROW_MAPPER = new WorkerRowMapper();
	private final String JOIN_COLUMN = "passport";
	private final String SELECT_SQL = String.format("SELECT * FROM %s JOIN %s USING (%s)", WORKERS_TABLE, PEOPLE_TABLE, JOIN_COLUMN);

	public DefaultWorkerRepository(DataSource dataSource) {
		jdbc = new JdbcTemplate(dataSource);
	}

	@Override
	@SneakyThrows
	public Optional<Worker> findBy(int id) {
		Worker worker = jdbc.queryForObject(SELECT_SQL + " WHERE id=?", ROW_MAPPER, id);
		return Optional.ofNullable(worker);
	}

	@Override
	@SneakyThrows
	public Collection<Worker> findAll() {
		return jdbc.query(SELECT_SQL, ROW_MAPPER);
	}

	@Override
	@SneakyThrows
	public Collection<Worker> find(int amount) {
		return find(amount, 0);
	}

	@Override
	@SneakyThrows
	public Collection<Worker> find(int amount, int offset) {
		String sql = SELECT_SQL + " LIMIT :amount OFFSET :offset";
		return jdbc.query(sql, ROW_MAPPER, amount, offset);
	}

	@Override
	public void update(Worker instance, String field, Object value) {
		jdbc.update("UPDATE " + WORKERS_TABLE + " SET :field=:value WHERE id=:id", field, value, instance.getId());
	}

	@Override
	public void delete(Worker instance) {
		final String passport = instance.getPerson().getPassport();
		final String deletePersonSql = new DeleteBuilder(PEOPLE_TABLE)
				.where("passport=" + passport)
				.toString();
		final String deleteWorkerSql = new DeleteBuilder(WORKERS_TABLE)
				.where("passport=" + passport)
				.toString();

		jdbc.update(deletePersonSql);
		jdbc.update(deleteWorkerSql);
	}

	@Override
	public void save(Worker instance) {
		final String insertPersonSql = buildInsertSql(instance.getPerson());
		jdbc.update(insertPersonSql);

		final String insertWorkerSql = buildInsertSql(instance);
		jdbc.update(insertWorkerSql);
	}

	private String buildInsertSql(Worker instance) {
		return new InsertBuilder(WORKERS_TABLE)
				.set("name", instance.getName())
				.set("x", String.valueOf(instance.getCoordinates().getX()))
				.set("y", String.valueOf(instance.getCoordinates().getY()))
				.set("salary", String.valueOf(instance.getSalary()))
				.set("hired", instance.getHired().toString())
				.set("quit", String.valueOf(instance.getQuit()))
				.set("status", instance.getStatus().toString())
				.set("passport", instance.getPerson().getPassport())
				.toString();
	}

	private String buildInsertSql(Person person) {
		return new InsertBuilder(PEOPLE_TABLE)
				.set("passport", person.getPassport())
				.set("eye_color", person.getEyeColor().toString())
				.set("hair_color", person.getHairColor().toString())
				.set("nationality", person.getNationality().toString())
				.toString();
	}

	private static class WorkerRowMapper implements RowMapper<Worker> {
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
			             .quit(Optional.ofNullable(rs.getTimestamp("quit")).map(Timestamp::toLocalDateTime).orElse(null))
			             .status(Worker.Status.valueOf(rs.getString("status")))
			             .person(Person.builder()
			                           .passport(rs.getString("passport"))
			                           .eyeColor(Person.EyeColor.valueOf(rs.getString("eye_color")))
			                           .hairColor(Person.HairColor.valueOf(rs.getString("hair_color")))
			                           .nationality(Country.valueOf(rs.getString("nationality")))
			                           .build())
			             .build();
		}
	}
}
