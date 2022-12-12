package com.mocksocialnetwork;

import Entities.User;
import Exceptions.ApiSpecific.LoginFailed;
import UserAPI.UserSession;
import Utils.SimpleEncoder;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField user_identification;
    @FXML
    private TextField user_password;
    @FXML
    private Button login_button;
    @FXML
    private Button register_button;

    @FXML
    protected void onRegisterButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("register-view.fxml"));
        Stage sourceStage = (Stage) register_button.getScene().getWindow();
        Scene register_scene = new Scene(fxmlLoader.load(), 400, 400);
        sourceStage.setScene(register_scene);
    }

    @FXML
    protected void onLoginButtonClicked() throws IOException{
        SimpleEncoder encoder = new SimpleEncoder();

        String identification = user_identification.getText();
        byte[] password = encoder.Encode(user_password.getText());

        user_identification.clear();
        user_password.clear();

        try{
            UserSession session = UserSession.getSession(identification, password);
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

            Stage sourceStage = (Stage) login_button.getScene().getWindow();
            Scene main_scene = new Scene(fxmlLoader.load(), 650, 500 );
            MainController controller = fxmlLoader.getController();

            sourceStage.hide();
            sourceStage.setScene(main_scene);

            controller.initView(session);

            sourceStage.show();

        } catch (LoginFailed e) {
            System.out.println(e.getMessage());
        }


    }


}