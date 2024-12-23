package ui;

import model.Person;

public class EditPersonCommand implements Command {
    private final Person person;
    private final String newName;

    public EditPersonCommand(Person person, String newName) {
        this.person = person;
        this.newName = newName;
    }

    @Override
    public void execute() {
        person.setName(newName);
    }
}
