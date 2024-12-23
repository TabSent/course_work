package ui;

import model.Person;
import service.Directory;

public class AddPersonCommand implements Command {
    private final Directory directory;
    private final Person person;

    public AddPersonCommand(Directory directory, Person person) {
        this.directory = directory;
        this.person = person;
    }

    @Override
    public void execute() {
        directory.addPerson(person);
    }
}
