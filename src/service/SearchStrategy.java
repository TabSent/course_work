// src/service/SearchStrategy.java
package service;

import model.Person;

import java.util.List;
import java.util.stream.Collectors;

public interface SearchStrategy {
    List<Person> search(List<Person> persons, String query);
}

// Example Strategy Implementation
class NameSearchStrategy implements SearchStrategy {
    @Override
    public List<Person> search(List<Person> persons, String query) {
        return persons.stream()
                .filter(person -> person.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
