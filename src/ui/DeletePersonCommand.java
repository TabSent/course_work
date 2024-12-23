package ui;

import model.Person;
import service.Directory;

public class DeletePersonCommand implements Command {
    private final Directory directory;
    private final Person person;

    public DeletePersonCommand(Directory directory, Person person) {
        this.directory = directory;
        this.person = person;
    }

    @Override
    public void execute() {
        directory.getPersons().remove(person);
    }
}
