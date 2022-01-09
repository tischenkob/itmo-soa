package ru.ifmo.worker.repo;

import ca.krasnay.sqlbuilder.DeleteBuilder;
import ca.krasnay.sqlbuilder.InsertBuilder;
import ca.krasnay.sqlbuilder.SelectBuilder;
import ca.krasnay.sqlbuilder.UpdateCreator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.ifmo.util.query.*;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static ru.ifmo.util.Nulls.map;
import static ru.ifmo.worker.api.Column.*;

@Repository
@RequiredArgsConstructor
public class DefaultWorkerRepository implements WorkerRepository {

	private final JdbcTemplate jdbc;
	private final RowMapper<Worker> WORKER_ROW_MAPPER;
	private final RowMapper<Group> GROUP_ROW_MAPPER;
	private final String JOIN_COLUMN = "passport";
	@Setter
	@Value("#{systemProperties['database.schema']}")
	private String schema;
	@Value("${database.tables.people}")
	private String peopleTable;
	@Value("${database.tables.workers}")
	private String workersTable;
	@Value("${database.tables.orgs}")
	private String orgsTable;
	private String SELECT_SQL;

	@PostConstruct
	void initialize() {
		if (schema != null && !schema.isEmpty()) {
			peopleTable = schema + "." + peopleTable;
			workersTable = schema + "." + workersTable;
			orgsTable = schema + "." + orgsTable;
		}
		SELECT_SQL = "SELECT w.id AS id,\n" +
					 "       w.name AS name,\n" +
					 "       w.x AS x,\n" +
					 "       w.y AS y,\n" +
					 "       w.created AS created,\n" +
					 "       w.salary AS salary,\n" +
					 "       w.hired AS hired,\n" +
					 "       w.quit AS quit,\n" +
					 "       w.status AS status,\n" +
					 "       p.passport AS passport,\n" +
					 "       p.eye_color AS eye_color,\n" +
					 "       p.hair_color AS hair_color,\n" +
					 "       p.nationality AS nationality,\n" +
					 "       o.id as org_id,\n" +
					 "       o.name AS org_name FROM " + workersTable + " w " +
					 "JOIN " + peopleTable + " p USING (" + JOIN_COLUMN + ") " +
					 "JOIN " + orgsTable + " o ON w.org_id = o.id";
	}

	@Override
	public Optional<Worker> findBy(int id) {
		List<Worker> workers = jdbc.query(SELECT_SQL + " WHERE w.id=?", WORKER_ROW_MAPPER, id);
		return workers.isEmpty() ? Optional.empty() : Optional.of(workers.get(0));
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

		for (Filter filter : parameters.getFilterSet()) {
			builder.and(filter.toString());
		}

		for (Sort sort : parameters.getSortSet()) {
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
				return format(" LIMIT %d OFFSET %d", page.getLimit(), page.getOffset());
			}
			return " OFFSET " + page.getOffset();
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
			return false;
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
			return false;
		}
	}

	@Override
	public List<Group> countGrouped() {
		try {
			return jdbc.query("SELECT count(*) as count, x, y FROM " + workersTable +
			                  " GROUP BY x, y", GROUP_ROW_MAPPER);
		} catch (DataAccessException e) {
			return emptyList();
		}
	}

	@Override
	public boolean update(Worker instance) {
		String passport = queryPassportOf(instance);
		Person person = instance.getPerson();
		UpdateCreator people = buildUpdateFor(person, passport);
		UpdateCreator workers = buildUpdateForWorker(instance);
		try {
			return 1 == jdbc.update(people) &&
			       1 == jdbc.update(workers);
		} catch (DataAccessException e) {
			return false;
		}
	}

	private String queryPassportOf(Worker instance) {
		String passport;
		try {
			passport = jdbc.queryForObject("SELECT passport FROM " + workersTable +
			                               " WHERE id = " + instance.getId(), String.class);
		} catch (DataAccessException e) {
			throw new IllegalArgumentException("Worker not found for id = " + instance.getId());
		}
		return passport;
	}

	private UpdateCreator buildUpdateFor(Person person, String passport) {
		return new UpdateCreator(peopleTable)
				.setValue(PASSPORT.getName(), person.getPassport())
				.setValue(EYE_COLOR.getName(), person.getEyeColor().toString())
				.setValue(HAIR_COLOR.getName(), person.getHairColor().toString())
				.setValue(NATIONALITY.getName(), person.getNationality().toString())
				.whereEquals(PASSPORT.getName(), passport);
	}

	private UpdateCreator buildUpdateForWorker(Worker instance) {
		return new UpdateCreator(workersTable)
				.setValue(NAME.getName(), instance.getName())
				.setValue(X.getName(), instance.getCoordinates().getX())
				.setValue(Y.getName(), instance.getCoordinates().getY())
				.setValue(SALARY.getName(), instance.getSalary())
				.setValue(HIRED.getName(), instance.getHired())
				.setValue(QUIT.getName(), instance.getQuit())
				.setValue(STATUS.getName(), instance.getStatus().toString())
				.whereEquals(ID.getName(), instance.getId());
	}

	@Override
	public Collection<String> findDistinctStatusValues() {
		String sql = "SELECT DISTINCT status FROM " + workersTable;
		try {
			return jdbc.queryForList(sql, String.class);
		} catch (DataAccessException e) {
			return emptyList();
		}
	}

	@Override
	public Collection<Worker> findNamedLike(String substring) {
		String sql = SELECT_SQL +
		             " WHERE " + NAME.getName() +
		             " LIKE '%" + substring + "%'";
		try {
			return jdbc.query(sql, WORKER_ROW_MAPPER);
		} catch (DataAccessException e) {
			return emptyList();
		}
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

}