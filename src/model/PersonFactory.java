// src/model/PersonFactory.java
package model;

public class PersonFactory {
    public static Person createPerson(String type, String name, String position) {
        return switch (type.toLowerCase()) {
            case "student" -> new Student(name, position);
            case "staff" -> new Staff(name, position);
            case "professor" -> new Professor(name, position);
            default -> throw new IllegalArgumentException("Unknown person type: " + type);
        };
    }
}
