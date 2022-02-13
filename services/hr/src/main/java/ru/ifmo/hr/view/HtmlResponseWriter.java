package ru.ifmo.hr.view;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Provider
@Produces(TEXT_HTML)
public class HtmlResponseWriter implements MessageBodyWriter<HtmlResponse> {

    private static final TemplateEngine templateEngine;

    static {
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(HtmlResponse.class);
    }

    @Override
    public long getSize(HtmlResponse a, Class<?> b, Type c, Annotation[] d, MediaType e) {
        return -1;
    }

    @Override
    public void writeTo(HtmlResponse response, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        Context context = new Context();
        context.setVariable("model", response.getContent());
        try (Writer writer = new OutputStreamWriter(entityStream)) {
            templateEngine.process(response.getTemplateName(), context, writer);
        }
    }

}