package com.mocksocialnetwork;

import Entities.*;
import Service.Service;
import UserAPI.UserSession;
import Utils.FriendshipStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    UserSession session = null;
    Service serv = Service.getInstance();

    EID selected = null;
    @FXML
    private Button backButton ;
    @FXML
    private ListView<Label> userView;

    @FXML
    private TextArea field;



    @FXML
    private ListView<HBox> messageView;


    /**/
    public Label convertUser(Friendship E){
        Label lbl = new Label(session.lookUpUserName(E.getUser()));
        lbl.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String labelText = lbl.getText();
                selected = session.lookUpEID(labelText);
                resetChatView(selected);
            }
        });

        return lbl;
    }

    public HBox convertMessage(Message m){
        HBox to_ret = new HBox();
        Label lbl = new Label(m.getMessage());
        to_ret.getChildren().add(lbl);
        if(m.getSender().getUsername().equals(session.getUserName())){
            to_ret.setAlignment(Pos.BASELINE_RIGHT);
        }else{
            to_ret.setAlignment(Pos.BASELINE_LEFT);
        }

        return to_ret;
    }

    private void resetChatView(EID other_user){
        List<Message> messageList = session.getMessages();

        List<Message> currentMessages = new ArrayList<>();

        for (Message m :
                messageList) {
            if(m.getSender().getUserId().equals(other_user) || m.getReceiver().getUserId().equals(other_user)){
                currentMessages.add(m);
            }
        }

        ObservableList<HBox> items = FXCollections.observableList(currentMessages.stream().map(E -> convertMessage(E)).toList());
        messageView.setItems(items);
        messageView.refresh();

    }

    @FXML
    private void onUserView(){
        String text = userView.getSelectionModel().getSelectedItem().getText();
        selected = session.lookUpEID(text);
        resetChatView(selected);
    }

    private void resetUserView(){
        ObservableList<Label> items = FXCollections.observableList(session.getFriends().stream().filter(E -> E.second.getStatus() == FriendshipStatus.FRIENDS || E.second.getStatus() == FriendshipStatus.PENDING).map(E -> convertUser(E.second)).toList());

        userView.setItems(items);
        userView.refresh();
    }

    public void initView(UserSession sess){
        session = sess;

        resetUserView();

    }

    @FXML
    public void onBackButton() throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        Stage sourceStage = (Stage) backButton.getScene().getWindow();
        Scene main_scene = new Scene(fxmlLoader.load(), 650, 500 );
        sourceStage.setMaxWidth(650);
        sourceStage.setMaxHeight(500);
        MainController controller = fxmlLoader.getController();

        sourceStage.hide();
        sourceStage.setScene(main_scene);

        controller.initView(session);

        sourceStage.show();

    }


    @FXML
    public void onSend() {
        String text = field.getText();
        if(!text.isEmpty()){
            field.clear();

            session.addMessage(new Message(session.retrieveUser(), session.retrieveUser(session.lookUpUserName(selected)), text, LocalDateTime.now()));

            resetChatView(selected);

        }
    }
}
