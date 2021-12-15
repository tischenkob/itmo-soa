package ru.ifmo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.util.query.Group;
import ru.ifmo.worker.CrudViewServlet;
import ru.ifmo.worker.ExtraViewServlet;
import ru.ifmo.worker.model.Coordinates;
import ru.ifmo.worker.model.Country;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.repo.DefaultWorkerRepository;
import ru.ifmo.worker.repo.WorkerRepository;
import ru.ifmo.worker.rest.CrudWorkerServlet;
import ru.ifmo.worker.rest.ExtraWorkerServlet;
import ru.ifmo.worker.service.DefaultWorkerService;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@WebListener
public class Application implements ServletContextListener {

	private static final String pathBase = "/api";
	private static final String heliosPropsPath = "/WEB-INF/helios.properties";
	private static final String dockerPropsPath = "/WEB-INF/docker.properties";

	private final String peopleTable = "soa_people";
	private final String workersTable = "soa_workers";
	private final String schemaPropertyName = "schema.name";
	private final String configurationPropertyName = "app.config";

	@Override
	@SneakyThrows
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		DataSource dataSource = importDataSource(context);

		String schema = System.getProperty(schemaPropertyName);
		schema = schema == null ? "" : schema + ".";

		WorkerRepository repository = new DefaultWorkerRepository(dataSource,
		                                                          schema + peopleTable,
		                                                          schema + workersTable);
		WorkerService service = new DefaultWorkerService(repository);
		XmlConverter xmlConverter = configureXmlConverter();

		ExtraWorkerServlet extraServlet = new ExtraWorkerServlet(service, xmlConverter);
		Dynamic extra = context.addServlet("Extra", extraServlet);
		extra.addMapping(pathBase + "/grouped",
		                 pathBase + "/named",
		                 pathBase + "/unique");

		CrudWorkerServlet crudServlet = new CrudWorkerServlet(service, xmlConverter);
		Dynamic worker = context.addServlet("Crud", crudServlet);
		worker.addMapping(pathBase + "/workers", pathBase + "/workers/*");

		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolverFrom(context));

		CrudViewServlet crudViewServlet = new CrudViewServlet(service, engine);
		Dynamic workerView = context.addServlet("CrudView", crudViewServlet);
		workerView.addMapping("/workers", "/workers/*");

		ExtraViewServlet extraViewServlet = new ExtraViewServlet(service, engine);
		Dynamic extraView = context.addServlet("ExtraView", extraViewServlet);
		extraView.addMapping("/extra", "/extra/*");
	}

	private AbstractConfigurableTemplateResolver templateResolverFrom(ServletContext context) {
		AbstractConfigurableTemplateResolver resolver = new ServletContextTemplateResolver(context);
		resolver.setPrefix("/WEB-INF/template/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML5");
		resolver.setCharacterEncoding("utf-8");
		return resolver;
	}

	@SneakyThrows
	private DataSource importDataSource(ServletContext context) {
		Properties properties = new Properties();
		String configuration = System.getProperties().getProperty(configurationPropertyName);
		String propsPath = "helios".equals(configuration)
		                   ? heliosPropsPath
		                   : dockerPropsPath;

		try (InputStream propsResource = context.getResourceAsStream(propsPath)) {
			properties.load(propsResource);
			PGSimpleDataSource dataSource = new PGSimpleDataSource();
			dataSource.setUrl(properties.getProperty("url"));
			dataSource.setUser(properties.getProperty("user"));
			dataSource.setPassword(properties.getProperty("password"));
			return dataSource;
		}
	}

	private XmlConverter configureXmlConverter() {
		XStream xmlConverter = new XStream(new StaxDriver());
		xmlConverter.addPermission(AnyTypePermission.ANY);
		setupAliases(xmlConverter);

		return new XmlConverter() {
			@Override
			public <T> String toXml(T object) {
				return xmlConverter.toXML(object);
			}

			@Override
			public <T> T fromXml(String xml, Class<T> clazz) {
				return clazz.cast(xmlConverter.fromXML(xml));
			}
		};
	}

	private void setupAliases(XStream xmlConverter) {
		xmlConverter.alias("worker", Worker.class);
		xmlConverter.alias("person", Person.class);
		xmlConverter.alias("coordinates", Coordinates.class);
		xmlConverter.alias("country", Country.class);
		xmlConverter.alias("status", Worker.Status.class);
		xmlConverter.aliasField("eye-color", Person.class, "eyeColor");
		xmlConverter.aliasField("hair-color", Person.class, "hairColor");
		xmlConverter.alias("group", Group.class);
		xmlConverter.alias("result", List.class);
		xmlConverter.alias("result", EnumSet.class);
		xmlConverter.alias("result", Set.class);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContextListener.super.contextDestroyed(sce);
	}
}
