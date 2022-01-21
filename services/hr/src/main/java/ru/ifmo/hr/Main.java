package ru.ifmo.hr;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/")
public class Main extends Application {
    /* class body intentionally left blank */

    @Override
    public Set<Object> getSingletons() {
        return super.getSingletons();
    }
}