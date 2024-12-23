// src/service/DirectoryUpdater.java
package service;

import model.Person;

public class DirectoryUpdater implements Runnable {
    private final Directory directory;
    private final Person person;

    public DirectoryUpdater(Directory directory, Person person) {
        this.directory = directory;
        this.person = person;
    }

    @Override
    public void run() {
        directory.addPerson(person);
        System.out.println("Added: " + person);
    }
}
