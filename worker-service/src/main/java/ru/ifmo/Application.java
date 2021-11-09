package ru.ifmo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;
import ru.ifmo.util.XmlConverter;
import ru.ifmo.worker.*;
import ru.ifmo.worker.model.Coordinates;
import ru.ifmo.worker.model.Country;
import ru.ifmo.worker.model.Person;
import ru.ifmo.worker.model.Worker;
import ru.ifmo.worker.repo.DefaultWorkerRepository;
import ru.ifmo.worker.repo.WorkerRepository;
import ru.ifmo.worker.service.DefaultWorkerService;
import ru.ifmo.worker.service.WorkerService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class Application implements ServletContextListener {

	private static final String pathBase = "/api";
	private static final String heliosPropsPath = "/WEB-INF/helios.properties";
	private static final String dockerPropsPath = "/WEB-INF/docker.properties";

	@Override
	@SneakyThrows
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		DataSource dataSource = importDataSource(context);

		WorkerRepository repository = new DefaultWorkerRepository(dataSource);
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
	}

	private XmlConverter configureXmlConverter() {
		XStream xmlConverter = new XStream(new StaxDriver());
		xmlConverter.alias("worker", Worker.class);
		xmlConverter.alias("person", Person.class);
		xmlConverter.alias("coordinates", Coordinates.class);
		xmlConverter.alias("country", Country.class);
		xmlConverter.alias("status", Worker.Status.class);
		xmlConverter.alias("eye-color", Person.EyeColor.class);
		xmlConverter.alias("hair-color", Person.HairColor.class);
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

	@SneakyThrows
	private DataSource importDataSource(ServletContext context) {
		Properties properties = new Properties();
		String configuration = System.getProperties().getProperty("app.config");
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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContextListener.super.contextDestroyed(sce);
	}
}
