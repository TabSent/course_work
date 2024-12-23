package main;

import model.Person;
import model.PersonFactory;
import repository.PersonDAO;
import service.Directory;
import ui.DirectoryApp;

import java.util.List;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Directory directory = Directory.getInstance();
        PersonDAO dao = new PersonDAO("persons.db");

        try {
            // Асинхронная загрузка данных
            Future<List<Person>> futurePersons = dao.loadPersonsAsync();
            directory.getPersons().addAll(futurePersons.get()); // Блокируем до завершения чтения

            // Добавление примеров (если требуется)
            if (directory.getPersons().isEmpty()) {
                addSampleData(directory);
                dao.savePersonsAsync(directory.getPersons()).get(); // Асинхронная запись
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.shutdown();
        }

        // Запуск графического интерфейса
        DirectoryApp.main(args);
    }

    private static void addSampleData(Directory directory) {
        directory.getPersons().addAll(List.of(
                PersonFactory.createPerson("student", "Иван Иванов", "Студент 1 курса"),
                PersonFactory.createPerson("professor", "Олег Григорьев", "Профессор")
        ));
    }
}
