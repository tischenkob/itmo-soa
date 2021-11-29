package ru.ifmo.worker.repo;

import ca.krasnay.sqlbuilder.DeleteBuilder;
import ca.krasnay.sqlbuilder.InsertBuilder;
import ca.krasnay.sqlbuilder.SelectBuilder;
import ca.krasnay.sqlbuilder.UpdateCreator;
import lombok.var;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.ifmo.util.query.*;
import ru.ifmo.worker.api.Column;
import ru.ifmo.worker.model.Coordinates;
import ru.ifmo.worker.model.Country;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static ru.ifmo.util.Nulls.map;

public class DefaultWorkerRepository implements WorkerRepository {

	private final JdbcTemplate jdbc;
	private final String peopleTable;
	private final String workersTable;
	private final RowMapper<Worker> WORKER_ROW_MAPPER = new WorkerRowMapper();
	private final RowMapper<Group> GROUP_ROW_MAPPER = new GroupRowMapper();
	private final String JOIN_COLUMN = "passport";
	private final String SELECT_SQL;

	public DefaultWorkerRepository(DataSource dataSource, String peopleTable, String workersTable) {
		jdbc = new JdbcTemplate(dataSource);
		this.peopleTable = peopleTable;
		this.workersTable = workersTable;
		SELECT_SQL = format("SELECT * FROM %s JOIN %s USING (%s)",
		                    workersTable, peopleTable, JOIN_COLUMN);
	}

	@Override
	public Optional<Worker> findBy(int id) {
		Worker worker = jdbc.queryForObject(SELECT_SQL + " WHERE id=?", WORKER_ROW_MAPPER, id);
		return Optional.ofNullable(worker);
	}

	@Override
	public Collection<Worker> findAll() {
		return jdbc.query(SELECT_SQL, WORKER_ROW_MAPPER);
	}

	@Override
	public List<Worker> findWith(QueryParameters parameters) {
		SelectBuilder builder = select()
				.from(workersTable)
				.join(peopleTable + " USING (" + JOIN_COLUMN + ")");

		for (Filter filter : parameters.getFilters()) {
			builder.and(filter.toString());
		}

		for (Sort sort : parameters.getSorts()) {
			boolean ascending = sort.getOrder() == Sort.Order.ASCENDING;
			builder.orderBy(sort.getField(), ascending);
		}

		String limit = buildLimitString(parameters.getPage());

		return jdbc.query(builder.toString() + limit, WORKER_ROW_MAPPER);
	}

	private SelectBuilder select() {
		return new SelectBuilder().column("*");
	}

	private String buildLimitString(Page page) {
		if (page != null) {
			if (page.getLimit() != 0) {
				return format("LIMIT %d OFFSET %d", page.getLimit(), page.getOffset());
			}
			return "OFFSET " + page.getOffset();
		}
		return "";
	}

	@Override
	public boolean save(Worker instance) {
		try {
			int insertedPeople = jdbc.update(insertFor(instance.getPerson()));
			int insertedWorkers = jdbc.update(insertFor(instance));
			return insertedPeople == 1 && insertedWorkers == 1;
		} catch (DataAccessException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

	}

	@Override
	public boolean deleteBy(int id) {
		final String deleteWorkerSql = new DeleteBuilder(workersTable)
				.where("id=" + id)
				.toString();

		try {
			return 1 == jdbc.update(deleteWorkerSql);
		} catch (DataAccessException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public List<Group> countGrouped() {
		try {
			return jdbc.query("SELECT count(*) as count, x, y FROM " + workersTable +
			                  " GROUP BY x, y", GROUP_ROW_MAPPER);
		} catch (DataAccessException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public boolean update(Worker instance) {
		String passport = queryPassportOf(instance);
		Person person = instance.getPerson();
		var people = buildUpdateFor(person, passport);
		var workers = buildUpdateForWorker(instance);
		try {
			return 1 == jdbc.update(people) &&
			       1 == jdbc.update(workers);
		} catch (DataAccessException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private String queryPassportOf(Worker instance) {
		String passport;
		try {
			passport = jdbc.queryForObject("SELECT passport FROM " + workersTable +
			                               " WHERE id = " + instance.getId(),
			                               String.class);
		} catch (DataAccessException e) {
			throw new IllegalArgumentException("Worker not found for id = " + instance.getId());
		}
		return passport;
	}

	private UpdateCreator buildUpdateFor(Person person, String passport) {
		return new UpdateCreator(peopleTable)
				.setValue(Column.PASSPORT.getName(), person.getPassport())
				.setValue(Column.EYE_COLOR.getName(), person.getEyeColor().toString())
				.setValue(Column.HAIR_COLOR.getName(), person.getHairColor().toString())
				.setValue(Column.NATIONALITY.getName(), person.getNationality().toString())
				.whereEquals(Column.PASSPORT.getName(), passport);
	}

	private UpdateCreator buildUpdateForWorker(Worker instance) {
		return new UpdateCreator(workersTable)
				.setValue(Column.NAME.getName(), instance.getName())
				.setValue(Column.X.getName(), instance.getCoordinates().getX())
				.setValue(Column.Y.getName(), instance.getCoordinates().getY())
				.setValue(Column.SALARY.getName(), instance.getSalary())
				.setValue(Column.HIRED.getName(), instance.getHired())
				.setValue(Column.QUIT.getName(), instance.getQuit())
				.setValue(Column.STATUS.getName(), instance.getStatus().toString())
				.whereEquals(Column.ID.getName(), instance.getId());
	}

	@Override
	public Collection<String> findDistinctStatusValues() {
		String sql = "SELECT DISTINCT status FROM " + workersTable;
		return jdbc.queryForList(sql, null, String.class);
	}

	@Override
	public Collection<Worker> findNamedLike(String substring) {
		String sql = SELECT_SQL +
		             " WHERE " + Column.NAME.getName() +
		             " LIKE '%" + substring + "%'";
		return jdbc.query(sql, WORKER_ROW_MAPPER);
	}

	private String insertFor(Person person) {
		return new InsertBuilder(peopleTable)
				.set("passport", quote(person.getPassport()))
				.set("eye_color", quote(person.getEyeColor()))
				.set("hair_color", quote(person.getHairColor()))
				.set("nationality", quote(person.getNationality()))
				.toString();
	}

	private String insertFor(Worker instance) {
		return new InsertBuilder(workersTable)
				.set("name", quote(instance.getName()))
				.set("x", String.valueOf(instance.getCoordinates().getX()))
				.set("y", String.valueOf(instance.getCoordinates().getY()))
				.set("salary", String.valueOf(instance.getSalary()))
				.set("hired", quote(instance.getHired()))
				.set("quit", map(instance.getQuit(), this::quote))
				.set("status", quote(instance.getStatus()))
				.set("passport", quote(instance.getPerson().getPassport()))
				.toString();
	}

	private String quote(String string) {
		return "'" + string + "'";
	}

	private String quote(Object object) {
		return quote(object.toString());
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
			             .quit(map(rs.getTimestamp("quit"), Timestamp::toLocalDateTime))
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

	private static class GroupRowMapper implements RowMapper<Group> {
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Group.builder()
			            .x(rs.getInt(Column.X.getName()))
			            .y(rs.getInt(Column.Y.getName()))
			            .count(rs.getInt("count"))
			            .build();
		}
	}
}
