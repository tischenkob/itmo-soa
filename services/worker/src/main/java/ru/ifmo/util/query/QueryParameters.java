package ru.ifmo.util.query;

import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class QueryParameters {
    private final Set<Filter> filterSet = new HashSet<>();
    private final Set<Sort> sortSet = new HashSet<>();
    Page page;

    public void add(Filter value) {
        filterSet.add(value);
    }

    public void add(Sort value) {
        sortSet.add(value);
    }

    public void set(Page value) {
        if (page != null) {
            throw new IllegalStateException("There cannot be more than one limit");
        }
        page = Objects.requireNonNull(value);
    }

    public boolean isEmpty() {
        return filterSet.isEmpty() && sortSet.isEmpty() && page == null;
    }
}