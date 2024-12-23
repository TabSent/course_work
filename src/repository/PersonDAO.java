package repository;

import model.Person;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PersonDAO {
    private final String filePath;
    private final ExecutorService executorService;

    public PersonDAO(String filePath) {
        this.filePath = filePath;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<List<Person>> loadPersonsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                return (List<Person>) ois.readObject();
            } catch (FileNotFoundException e) {
                return new ArrayList<>(); // Если файла нет, возвращаем пустой список
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Ошибка при чтении файла", e);
            }
        });
    }

    public CompletableFuture<Void> savePersonsAsync(List<Person> persons) {
        return CompletableFuture.runAsync(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(persons);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при записи файла", e);
            }
        });
    }


    public void shutdown() {
        executorService.shutdown();
    }
}
