package com.patientsfx.controller;

import com.patientsfx.model.Patient;
import com.patientsfx.service.PatientApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientController {
    private final PatientApiService apiService;
    private final ObservableList<Patient> patients;
    private final TableView<Patient> tableView;
    private final TextField nameField;
    private final DatePicker birthDatePicker;
    private final TextField diagnosisField;
    private final Label statusLabel;
    private Patient selectedPatient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PatientController() {
        this.apiService = new PatientApiService();
        this.patients = FXCollections.observableArrayList();
        this.tableView = createTableView();
        this.nameField = new TextField();
        this.birthDatePicker = new DatePicker();
        this.diagnosisField = new TextField();
        this.statusLabel = new Label();
        this.selectedPatient = null;

        loadPatients();
    }

    private TableView<Patient> createTableView() {
        TableView<Patient> table = new TableView<>();
        table.setItems(patients);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Patient, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<Patient, LocalDate> birthDateColumn = new TableColumn<>("Дата рождения");
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        birthDateColumn.setCellFactory(column -> new TableCell<Patient, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            }
        });
        birthDateColumn.setPrefWidth(150);

        TableColumn<Patient, String> diagnosisColumn = new TableColumn<>("Диагноз");
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        diagnosisColumn.setPrefWidth(300);

        table.getColumns().addAll(idColumn, nameColumn, birthDateColumn, diagnosisColumn);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPatient = newSelection;
                fillForm(newSelection);
            }
        });

        return table;
    }

    private void fillForm(Patient patient) {
        nameField.setText(patient.getName());
        birthDatePicker.setValue(patient.getBirthDate());
        diagnosisField.setText(patient.getDiagnosis());
    }

    private void clearForm() {
        nameField.clear();
        birthDatePicker.setValue(null);
        diagnosisField.clear();
        selectedPatient = null;
        tableView.getSelectionModel().clearSelection();
    }

    private void loadPatients() {
        try {
            List<Patient> patientList = apiService.getAllPatients();
            patients.clear();
            patients.addAll(patientList);
            showStatus("Пациенты загружены успешно", Color.GREEN);
        } catch (Exception e) {
            showStatus("Ошибка загрузки: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void createPatient() {
        try {
            if (nameField.getText().trim().isEmpty() || diagnosisField.getText().trim().isEmpty()) {
                showStatus("Заполните все обязательные поля", Color.RED);
                return;
            }

            if (birthDatePicker.getValue() == null) {
                showStatus("Выберите дату рождения", Color.RED);
                return;
            }

            if (birthDatePicker.getValue().isAfter(LocalDate.now())) {
                showStatus("Дата рождения не может быть в будущем", Color.RED);
                return;
            }

            Patient patient = new Patient(
                    nameField.getText().trim(),
                    birthDatePicker.getValue(),
                    diagnosisField.getText().trim()
            );

            apiService.createPatient(patient);
            loadPatients();
            clearForm();
            showStatus("Пациент добавлен успешно", Color.GREEN);
        } catch (Exception e) {
            showStatus("Ошибка добавления: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void updatePatient() {
        try {
            if (selectedPatient == null) {
                showStatus("Выберите пациента для обновления", Color.RED);
                return;
            }

            if (nameField.getText().trim().isEmpty() || diagnosisField.getText().trim().isEmpty()) {
                showStatus("Заполните все обязательные поля", Color.RED);
                return;
            }

            if (birthDatePicker.getValue() == null) {
                showStatus("Выберите дату рождения", Color.RED);
                return;
            }

            if (birthDatePicker.getValue().isAfter(LocalDate.now())) {
                showStatus("Дата рождения не может быть в будущем", Color.RED);
                return;
            }

            Patient patient = new Patient(
                    nameField.getText().trim(),
                    birthDatePicker.getValue(),
                    diagnosisField.getText().trim()
            );

            apiService.updatePatient(selectedPatient.getId(), patient);
            loadPatients();
            clearForm();
            showStatus("Пациент обновлен успешно", Color.GREEN);
        } catch (Exception e) {
            showStatus("Ошибка обновления: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void deletePatient() {
        try {
            if (selectedPatient == null) {
                showStatus("Выберите пациента для удаления", Color.RED);
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удалить пациента?");
            alert.setContentText("Вы уверены, что хотите удалить этого пациента?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                apiService.deletePatient(selectedPatient.getId());
                loadPatients();
                clearForm();
                showStatus("Пациент удален успешно", Color.GREEN);
            }
        } catch (Exception e) {
            showStatus("Ошибка удаления: " + e.getMessage(), Color.RED);
            e.printStackTrace();
        }
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    public VBox getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Заголовок
        Label titleLabel = new Label("Управление пациентами");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        // Таблица
        VBox tableBox = new VBox(5);
        Label tableLabel = new Label("Список пациентов:");
        tableBox.getChildren().addAll(tableLabel, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Форма
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");

        Label formLabel = new Label("Форма пациента:");
        formLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        formGrid.add(new Label("Имя:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Дата рождения:"), 0, 1);
        formGrid.add(birthDatePicker, 1, 1);
        formGrid.add(new Label("Диагноз:"), 0, 2);
        formGrid.add(diagnosisField, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(col1, col2);

        // Кнопки
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Добавить");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> createPatient());

        Button updateButton = new Button("Обновить");
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(e -> updatePatient());

        Button deleteButton = new Button("Удалить");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> deletePatient());

        Button clearButton = new Button("Очистить");
        clearButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearForm());

        Button refreshButton = new Button("Обновить список");
        refreshButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadPatients());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton, refreshButton);

        formBox.getChildren().addAll(formLabel, formGrid, buttonBox);

        // Статус
        statusLabel.setStyle("-fx-font-size: 12px;");
        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);

        // Разделитель
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(tableBox, formBox);
        splitPane.setDividerPositions(0.6);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        root.getChildren().addAll(titleBox, splitPane, statusBox);

        return root;
    }
}







