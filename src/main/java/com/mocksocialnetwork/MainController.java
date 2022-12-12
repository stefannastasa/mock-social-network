package com.mocksocialnetwork;

import Entities.Friendship;
import Entities.ProtectedUser;
import UserAPI.UserSession;
import Utils.FriendshipStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;

public class MainController {

    UserSession session;

    @FXML
    private Label u_title;
    @FXML
    private Button friends_button;

    @FXML
    private ListView<HBox> friendsListView;

    @FXML
    private TextField searchBar;
    public void initView(UserSession sess){
        this.session = sess;
        u_title.setWrapText(true);
        u_title.setText("Hello, " + sess.getName() + "!");
        resetFriendsView();
    }

    private HBox translateUser(Friendship user){
        System.out.println(user.getDateTime().toString());
        long hours = ChronoUnit.HOURS.between( user.getDateTime(), LocalDateTime.now());
        Label friendship_date;
        if(hours > 24){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            friendship_date = new Label(formatter.format(user.getDateTime()).toString());

        }else{
            if(hours > 1)
                friendship_date = new Label(hours + " hour ago");
            else
                friendship_date = new Label(hours + " hours ago");
        }

        HBox to_ret = new HBox();

        to_ret.setMinWidth(friendsListView.getWidth());
        Label user_data = new Label(session.lookUpName(user.getUser()));


        Button friendship_status = new Button(user.getStatus().toString());

        if(user.getStatus() == FriendshipStatus.PENDING){
            friendship_status.setDisable(true);
        }

        to_ret.getChildren().add(user_data);
        to_ret.getChildren().add(friendship_date);
        to_ret.getChildren().add(friendship_status);
        to_ret.setMinWidth(friendsListView.getWidth());
        to_ret.setAlignment(Pos.CENTER);
        to_ret.setSpacing(20.0);
        return to_ret;

    }

    private void resetFriendsView(){
        ObservableList<HBox> items = FXCollections.observableList(session.getFriends().stream().map(E -> translateUser(E.second)).toList());

        friendsListView.setItems(items);
    }

    @FXML
    protected void onPromptClick(){
        friendsListView.setItems(FXCollections.observableArrayList());
    }

    @FXML
    protected void onInput(){
//        if(searchBar.getText().equals("")){
//            onPromptClick();
//            return;
//        }
//        ObservableList<String> items = FXCollections.observableList(session.searchUsers(searchBar.getText()).stream().map(E -> E.getName()).toList());
//        friendsListView.setItems(items);
//        friendsListView.refresh();
    }
    @FXML
    protected void onRelease(){
        friendsListView.refresh();
    }

    @FXML
    protected void addFriend(){


    }

}
