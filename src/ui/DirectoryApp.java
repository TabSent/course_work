package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Person;
import model.PersonFactory;
import service.Directory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryApp extends Application {
    private final Directory directory = Directory.getInstance();
    private final ListView<String> personListView = new ListView<>();
    private final CommandInvoker commandInvoker = new CommandInvoker();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Электронный справочник кафедры");

        BorderPane root = new BorderPane();
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        TextField nameField = new TextField();
        nameField.setPromptText("Имя");

        TextField positionField = new TextField();
        positionField.setPromptText("Должность");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Student", "Staff", "Professor");
        typeBox.setPromptText("Тип");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> addPerson(nameField, positionField, typeBox));

        panel.getChildren().addAll(
                new Label("Добавить пользователя"),
                nameField,
                positionField,
                typeBox,
                addButton
        );

        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Поиск по имени");

        Button searchButton = new Button("Найти");
        searchButton.setOnAction(e -> searchByName(searchField));

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> deletePersonWithConfirmation());

        Button editButton = new Button("Редактировать");
        editButton.setOnAction(e -> editPerson());

        // Горизонтальный ряд кнопок
        HBox actionButtons = new HBox(10);
        actionButtons.getChildren().addAll(searchButton, deleteButton, editButton);

        ComboBox<String> typeFilterBox = new ComboBox<>();
        typeFilterBox.getItems().addAll("All", "Student", "Staff", "Professor");
        typeFilterBox.setValue("All");

        ComboBox<String> sortTypeBox = new ComboBox<>();
        sortTypeBox.getItems().addAll("None", "By Name", "By Type");
        sortTypeBox.setValue("None");

        Button filterSortButton = new Button("Применить фильтр и сортировку");
        filterSortButton.setOnAction(e -> searchAndSortPersons(searchField, typeFilterBox, sortTypeBox));

        personListView.setPrefHeight(500);
        refreshList();

        panel.getChildren().addAll(
                new Label("Список пользователей"),
                personListView,
                new Label("Поиск"),
                searchField,
                actionButtons, // Добавили кнопки в один ряд
                new Label("Фильтрация"),
                typeFilterBox,
                new Label("Сортировка"),
                sortTypeBox,
                filterSortButton
        );

        return panel;
    }

    private void addPerson(TextField nameField, TextField positionField, ComboBox<String> typeBox) {
        String name = nameField.getText();
        String position = positionField.getText();
        String type = typeBox.getValue();

        if (name.isEmpty() || position.isEmpty() || type == null) {
            showAlert("Ошибка", "Все поля должны быть заполнены.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Person person = PersonFactory.createPerson(type.toLowerCase(), name, position);
            Command addCommand = new AddPersonCommand(directory, person);
            commandInvoker.invoke(addCommand);
            refreshList();
            showAlert("Успех", "Пользователь успешно добавлен.", Alert.AlertType.INFORMATION);
            nameField.clear();
            positionField.clear();
            typeBox.setValue(null);
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshList() {
        personListView.getItems().setAll(directory.getPersons().stream()
                .map(Person::toString)
                .collect(Collectors.toList()));
    }

    private void searchByName(TextField searchField) {
        String query = searchField.getText().toLowerCase();

        List<Person> filteredPersons = directory.getPersons().stream()
                .filter(person -> person.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        personListView.getItems().setAll(filteredPersons.stream()
                .map(Person::toString)
                .collect(Collectors.toList()));
    }

    private void searchAndSortPersons(TextField searchField, ComboBox<String> typeFilterBox, ComboBox<String> sortTypeBox) {
        String query = searchField.getText().toLowerCase();
        String typeFilter = typeFilterBox.getValue();
        String sortType = sortTypeBox.getValue();

        List<Person> filteredPersons = directory.getPersons();

        if (!query.isEmpty()) {
            filteredPersons = filteredPersons.stream()
                    .filter(person -> person.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        if (!typeFilter.equals("All")) {
            filteredPersons = filteredPersons.stream()
                    .filter(person -> person.getClass().getSimpleName().equalsIgnoreCase(typeFilter))
                    .collect(Collectors.toList());
        }

        switch (sortType) {
            case "By Name" -> filteredPersons.sort(Comparator.comparing(Person::getName));
            case "By Type" -> filteredPersons.sort(Comparator.comparing(person -> person.getClass().getSimpleName()));
        }

        personListView.getItems().setAll(filteredPersons.stream()
                .map(Person::toString)
                .collect(Collectors.toList()));
    }

    private void deletePersonWithConfirmation() {
        String selectedPerson = personListView.getSelectionModel().getSelectedItem();

        if (selectedPerson == null) {
            showAlert("Ошибка", "Выберите пользователя для удаления.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение удаления");
        confirmationAlert.setHeaderText("Вы уверены, что хотите удалить?");
        confirmationAlert.setContentText(selectedPerson);

        if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Person person = directory.getPersons().stream()
                    .filter(p -> p.toString().equals(selectedPerson))
                    .findFirst()
                    .orElse(null);

            if (person != null) {
                Command deleteCommand = new DeletePersonCommand(directory, person);
                commandInvoker.invoke(deleteCommand);
                refreshList();
                showAlert("Успех", "Пользователь успешно удалён.", Alert.AlertType.INFORMATION);
            }
        }
    }

    private void editPerson() {
        String selectedPerson = personListView.getSelectionModel().getSelectedItem();

        if (selectedPerson == null) {
            showAlert("Ошибка", "Выберите пользователя для редактирования.", Alert.AlertType.ERROR);
            return;
        }

        Person person = directory.getPersons().stream()
                .filter(p -> p.toString().equals(selectedPerson))
                .findFirst()
                .orElse(null);

        if (person == null) {
            showAlert("Ошибка", "Пользователь не найден.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dialog = new TextInputDialog(person.getName());
        dialog.setTitle("Редактирование");
        dialog.setHeaderText("Редактировать имя пользователя");
        dialog.setContentText("Новое имя:");

        dialog.showAndWait().ifPresent(newName -> {
            Command editCommand = new EditPersonCommand(person, newName);
            commandInvoker.invoke(editCommand);
            refreshList();
            showAlert("Успех", "Пользователь успешно обновлён.", Alert.AlertType.INFORMATION);
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
