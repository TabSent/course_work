package service;

import model.Person;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

public class Directory {
    private static Directory instance;
    private final List<Person> persons;
    private final List<Runnable> observers = new ArrayList<>(); // Список наблюдателей

    private Directory() {
        this.persons = new CopyOnWriteArrayList<>();
    }

    public static synchronized Directory getInstance() {
        if (instance == null) {
            instance = new Directory();
        }
        return instance;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void addPerson(Person person) {
        persons.add(person);
        notifyObservers(); // Уведомление наблюдателей
    }

    public void removePerson(Person person) {
        persons.remove(person);
        notifyObservers(); // Уведомление наблюдателей
    }

    // Методы управления наблюдателями
    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    public void removeObserver(Runnable observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Runnable observer : observers) {
            observer.run();
        }
    }
}
