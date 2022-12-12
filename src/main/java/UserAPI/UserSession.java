package UserAPI;

import Entities.EID;
import Entities.Friendship;
import Entities.ProtectedUser;
import Entities.User;
import Exceptions.ApiSpecific.LoginFailed;
import Service.Service;
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

    public void rejectFriendship(EID user){
        try {
            app_service.removeFriendship(subject.getUserId(), user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProtectedUser> searchUsers(String input){
        return app_service.searchUsers(input)
                .stream().filter(E ->
                        !E.getUser_id().equals( subject.getUserId() ) ).toList();
    }

    public String lookUpName(EID user){
        return app_service.EidLookUpName(user);
    }

}