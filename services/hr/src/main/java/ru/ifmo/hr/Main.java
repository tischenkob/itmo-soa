package ru.ifmo.hr;

import lombok.Getter;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import ru.ifmo.hr.worker.Workers;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;

@ApplicationPath("/")
public class Main extends Application {
    @Getter
    private static Workers workers;
    private final String certificatePath = System.getProperty("ssl.cert");
    private final String password = System.getProperty("ssl.pass");
    private final String workerLocation = System.getProperty("services.worker.uri");

    @PostConstruct
    @SneakyThrows
    private void init() {
        URI workerUri = new URI(workerLocation);
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream certificate = new FileInputStream(certificatePath)) {
            trustStore.load(certificate, password.toCharArray());
            workers = RestClientBuilder.newBuilder()
                .baseUri(workerUri)
                .trustStore(trustStore)
                .hostnameVerifier((i, gnore) -> true)
                .build(Workers.class);
        }
    }
}