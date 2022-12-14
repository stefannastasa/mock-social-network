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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
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
    private Button logout_button;
    @FXML
    private TextField searchBar;
    public void initView(UserSession sess){
        this.session = sess;
        u_title.setWrapText(true);
        u_title.setText("Hello, " + sess.getName() + "!");
        resetFriendsView();
    }

    private HBox translateFriend(Friendship user){
        long hours = ChronoUnit.HOURS.between( user.getDateTime(), LocalDateTime.now());
        long minutes = ChronoUnit.MINUTES.between(user.getDateTime(), LocalDateTime.now());
        Label friendship_date = null;
        if(hours > 24){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            friendship_date = new Label(formatter.format(user.getDateTime()).toString());

        }else{
            if(hours > 2) {
                friendship_date = new Label(hours + " hours ago");
            }
            else if(hours == 1) {
                friendship_date = new Label(hours + " hour ago");
            }
            else if (hours == 0){
                friendship_date = new Label(minutes + "minutes ago");
            }

        }

        HBox to_ret = new HBox();

        to_ret.setMinWidth(friendsListView.getWidth());
        Label user_data = new Label(session.lookUpName(user.getUser()));

        Button friendship_button2 = null;

        if (user.getStatus() == FriendshipStatus.FRIENDS){
            friendship_button2 = new Button("REMOVE");

            friendship_button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        session.removeFriend(user.getUser());
                        resetFriendsView();
                        searchBar.clear();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });

        } else if(user.getStatus() == FriendshipStatus.PENDING){
            friendship_button2 = new Button("ACCEPT");
            friendship_button2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    session.acceptFriendship(user.getUser());
                    resetFriendsView();
                    searchBar.clear();
                }
            });
        }



        to_ret.getChildren().add(user_data);
        to_ret.getChildren().add(friendship_date);
        to_ret.getChildren().add(friendship_button2);
        to_ret.setMinWidth(friendsListView.getWidth());
        to_ret.setAlignment(Pos.CENTER);
        to_ret.setSpacing(20.0);
        return to_ret;

    }

    private HBox translateUser(ProtectedUser user, String search){
        HBox to_ret = new HBox();

        Label name = new Label(user.getName());
        Button userStatus = null;
        Button userAction = null;
        if(session.checkFriend(user.getUser_id()) == FriendshipStatus.FRIENDS){
            userStatus = new Button("FRIENDS");
            userStatus.setDisable(true);

            userAction = new Button("REMOVE");
            userAction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        session.removeFriend(user.getUser_id());
                        resetUsersView(search);
                        searchBar.clear();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }else if(session.checkFriend(user.getUser_id()) == null){
            userAction = new Button("ADD AS FRIEND");
            userAction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try{
                        session.sendFriendRequest(user.getUser_id());
                        resetUsersView(search);
                        searchBar.clear();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }else if(session.checkFriend(user.getUser_id()) == FriendshipStatus.SENT){
            userAction = new Button("SENT");
            userAction.setDisable(true);
        }else if(session.checkFriend(user.getUser_id()) == FriendshipStatus.PENDING){
            userAction = new Button("ACCEPT");
            userAction.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    session.acceptFriendship(user.getUser_id());
                    resetUsersView(search);
                    searchBar.clear();
                }
            });
        }

        to_ret.getChildren().add(name);
        if(userStatus != null){
            to_ret.getChildren().add(userStatus);
        }
        to_ret.getChildren().add(userAction);
        to_ret.setMinWidth(friendsListView.getWidth());
        to_ret.setAlignment(Pos.CENTER);
        to_ret.setSpacing(20.0);

        return to_ret;
    }

    private void resetFriendsView(){
        ObservableList<HBox> items = FXCollections.observableList(session.getFriends().stream().filter(E -> E.second.getStatus() == FriendshipStatus.FRIENDS || E.second.getStatus() == FriendshipStatus.PENDING).map(E -> translateFriend(E.second)).toList());

        friendsListView.setItems(items);
        friendsListView.refresh();
        searchBar.clear();
    }

    private void resetUsersView(String search){
        ObservableList<HBox> items = FXCollections.observableList(session.searchUsers(search).stream().map(E -> translateUser(E, search)).toList());
        friendsListView.setItems(items);
        friendsListView.refresh();
        searchBar.clear();
    }
    @FXML
    protected void onPromptClick(){
        friendsListView.setItems(FXCollections.observableArrayList());
    }

    @FXML
    protected void onInput(){
        if(searchBar.getText().equals("")){
            onPromptClick();
            return;
        }
        ObservableList<HBox> items = FXCollections.observableList(session.searchUsers(searchBar.getText()).stream().map(E -> translateUser(E, searchBar.getText())).toList());
        friendsListView.setItems(items);
        friendsListView.refresh();
    }
    @FXML
    protected void onRelease(){
        friendsListView.refresh();
    }

    @FXML
    protected void onFriendsButtonClick(){
        resetFriendsView();
    }

    @FXML
    protected void onLogOutButton(){
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        try{
            Stage sourceStage = (Stage) logout_button.getScene().getWindow();
            Scene main_scene = new Scene(fxmlLoader.load(), 400, 400);
            sourceStage.setScene(main_scene);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

}
