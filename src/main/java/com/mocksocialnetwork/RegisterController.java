package com.mocksocialnetwork;

import Service.Service;
import Utils.SimpleEncoder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    Service app_serv = Service.getInstance();

    @FXML
    private TextField name_field;

    @FXML
    private TextField username_field;

    @FXML
    private TextField email_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button register_button;

    @FXML
    protected void onRegisterClick(){
        SimpleEncoder encoder = new SimpleEncoder();
        String name, username, email, password;
        password = password_field.getText();

        name = name_field.getText();
        name_field.clear();

        username = username_field.getText();
        username_field.clear();

        email = email_field.getText();
        email_field.clear();

        try {
            app_serv.addUser(name, username, email, password);

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));

            Stage sourceStage = (Stage) register_button.getScene().getWindow();
            Scene login_scene = new Scene(fxmlLoader.load(), 650, 500 );
            sourceStage.setScene(login_scene);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
