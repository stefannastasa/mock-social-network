package UserAPI;

import Entities.*;
import Exceptions.ApiSpecific.LoginFailed;
import Exceptions.RepoSpecific.ElementExistsException;
import Service.Service;
import Utils.FriendshipStatus;
import Utils.pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserSession {
    private static final Service app_service = Service.getInstance();
    private final User subject;
    private UserSession(User subject) {
        this.subject = subject;
    }


    public static UserSession getSession(String identification, byte[] password) throws LoginFailed {
        User potential_user = app_service.getUser(identification);
        if(potential_user == null || !Arrays.equals(potential_user.getPasswordHash(), password))
            throw new LoginFailed();

        return new UserSession(potential_user);
    }

    public String getName(){
        return this.subject.getName();
    }
    public String getUserName(){
        return this.subject.getUsername();
    }

    public List<pair<String,Friendship>> getFriends(){
            return app_service.getUserFriends(subject);
    }

    public void acceptFriendship(EID user){
        app_service.acceptFriendship(subject.getUserId(), user);
    }
    public void sendFriendRequest(EID user) throws Exception {
        app_service.addFriendship(subject.getUserId(), user);
    }
    public List<ProtectedUser> searchUsers(String input){
        return app_service.searchUsers(input)
                .stream().filter(E ->
                        !E.getUser_id().equals( subject.getUserId() ) ).toList();
    }

    public String lookUpName(EID user){
        return app_service.EidLookUpName(user);
    }

    public String lookUpUserName(EID user){
        return app_service.EidLookUpUsername(user);
    }

    public void removeFriend(EID user) throws Exception {
        app_service.removeFriendship(subject.getUserId(), user);
    }
    public FriendshipStatus checkFriend(EID user){
        pair<String, Friendship> to_ret = app_service.getUserFriends(subject).stream().filter(E -> E.second.getUser().equals(user)).findAny().orElse(null);
        if(to_ret == null){
            return null;
        }else{
            return to_ret.second.getStatus();
        }
    }

    public List<Message> getMessages(){
        return app_service.getUserMessages(subject.getUserId());
    }

    public void addMessage(Message m){
        try {
            app_service.addMessage(m);
        } catch (ElementExistsException e) {
            e.printStackTrace();
        }
    }

    public EID lookUpEID(String user_name){
        return app_service.getUser(user_name).getUserId();
    }
    public User retrieveUser(){
        return subject;
    }

    public User retrieveUser(String user_name){
        return app_service.getUser(user_name);
    }
}
