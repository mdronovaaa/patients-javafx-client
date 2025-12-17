package com.patientsfx;

import com.patientsfx.controller.PatientController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        PatientController controller = new PatientController();
        Scene scene = new Scene(controller.getView(), 1000, 650);
        
        primaryStage.setTitle("Управление пациентами");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}







