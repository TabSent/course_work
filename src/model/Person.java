// src/model/Person.java
package model;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private String name;
    private String position;

    public Person(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, position);
    }
}
