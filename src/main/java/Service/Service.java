package Service;

import Entities.*;
import Exceptions.RepoSpecific.ElementExistsException;
import Exceptions.SocialNetworkException;
import Repository.MessageRepository;
import Repository.UserRepository;
import Repository.FriendshipRepository;

import Strategies.InputValidator;
import Strategies.Strategy;
import Utils.FriendshipStatus;
import Utils.pair;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Service {
    private final UserRepository UserRepo = new UserRepository();
    private final FriendshipRepository FriendsRepo = new FriendshipRepository();

    private final MessageRepository MessRepo = new MessageRepository();
    private final Strategy validator = new InputValidator();

    private static Service instance = null;

    private Service(){}

    public static Service getInstance(){
        if(instance == null){
            instance = new Service();
        }

        return instance;
    }

    /**
     * Validates then adds a user with the given values to the application.
     * @param name the new user's name
     * @param userName the new user's userName
     * @param email the new user's email
     * @param password the new user's password
     *
     * */
    public void addUser(String name, String userName, String email, String password) throws Exception {
        validator.setData(name, userName, email, password);
        validator.execute();


        User U = new User(email, userName, name, password);
        UserRepo.addElem(U);
        FriendsRepo.addElem(new Friendships(U.getUserId()));
    }

    public void addFriendship(EID user1, EID user2) throws Exception {
        FriendsRepo.addFriends(user1, user2);
    }

    /**
     * Removes the user with the given userName and email combination
     * @param userName username of the user to be removed
     * @param email email of the user to be removed
     *
     * @throws SocialNetworkException in case of user not existing or data not being valid
     * */
    public void removeUser(String userName, String email) throws Exception {
        validator.setData(null, userName, email,null);
        validator.execute();
        EID key = new EID(email + userName);

        UserRepo.removeElem(key);

        FriendsRepo.removeElem(key);
        FriendsRepo.getStream().forEach(E -> {
            try {
                FriendsRepo.removeFriends(E.getUserId(), key);
            } catch (SocialNetworkException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void removeFriendship(EID user1, EID user2) throws Exception {

        FriendsRepo.removeFriends(user1, user2);

    }


    /**
     * Gathers the number of communities in the social network.
     * @return int
     * */
    public int getNrComunitati(){
        int community_count = 0;
        ConcurrentHashMap<EID, Boolean> passed = new ConcurrentHashMap<>();
        LinkedList<EID> buffer = new LinkedList<>();
        UserRepo.getStream().forEach(E -> passed.put(E.getUserId(), false));
        Set<EID> list = passed.keySet();
        for (EID user : list) {

            if(!passed.get(user)){
                community_count++;
                buffer.add(user);
            }

            EID current ;
            while(!buffer.isEmpty()){
                current = buffer.remove();
                if(!passed.get(current)){
                    passed.put(current, true);
                    if(FriendsRepo.lookUp(current) != null){
                        FriendsRepo.lookUp(current).getStream().forEach(friend -> buffer.add(friend.getUser()));
                    }
                }
            }
        }

        return community_count;

    }
    /**
     * Private function. Searches for the longest path with unique nodes from  the given node.
     * */
    private void DFS(EID start, List<EID> base ){
        base.add(start);
        if( FriendsRepo.lookUp(start) == null){
            return;
        }
        List<EID> friends = FriendsRepo.lookUp(start).getStream().map(E -> E.getUser()).toList();
        for (EID friend :
                friends) {
            if(!base.contains(friend)){
                List<EID> aux = base;
                DFS(friend, aux);
                if(aux.size() > base.size())
                    base = aux;
            }
        }

    }

    /**
     * Gathers the list of users in the most active community.
     * @return List
     * */
    public List<User> getMostActiveCommunity(){
        ConcurrentHashMap<EID, Boolean> passed = new ConcurrentHashMap<>();
        UserRepo.getStream().forEach(E -> passed.put(E.getUserId(), false));
        List<EID> answer = new ArrayList<>();
        for (EID friend :
                passed.keySet()) {
            if (!passed.get(friend)) {
                passed.put(friend, true);
                List<EID> aux = new ArrayList<>();
                DFS(friend, aux);
                if (answer.size() < aux.size())
                    answer = aux;
            }
        }
        List<User> Users = new ArrayList<>();

        for (EID user : answer) {
            Users.add(UserRepo.lookUp(user));
        }

        return Users;


    }

    public User getUser(String identification){
        int checks = 0;
        User findUser = null;
        try{
            validator.checkMail(identification);
            findUser = UserRepo.getStream().filter(E -> E.getEmail().equals(identification)).findFirst().orElse(null);

        } catch (Exception e){
            checks++;
        }
        if(checks == 1){
            try{
                validator.checkUserName(identification);
                findUser = UserRepo.getStream().filter(E -> E.getUsername().equals(identification)).findFirst().orElse(null);
            } catch (Exception ex) {
                checks++;
            }
        }

        return findUser;
    }

    public List<pair<String,Friendship>> getUserFriends(User user){
        if(FriendsRepo.lookUp(user.getUserId()) == null){
            return List.of();
        }
        return FriendsRepo.lookUp(user.getUserId()).getStream().map(E -> new pair<>(EidLookUpName(E.getUser()), E)).toList();
    }
    public String EidLookUpName(EID user){
        return UserRepo.lookUp(user).getName();
    }

    public String EidLookUpUsername(EID user){
        return UserRepo.lookUp(user).getUsername();
    }

    public void acceptFriendship(EID user1, EID user2){
        try {
            FriendsRepo.updateFriendship(user1, user2, LocalDateTime.now(), FriendshipStatus.FRIENDS);
            FriendsRepo.updateFriendship(user2, user1, LocalDateTime.now(), FriendshipStatus.FRIENDS);
        } catch (SocialNetworkException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProtectedUser> searchUsers(String input){
        return UserRepo.getStream().filter(E -> E.getUsername().contains(input) || E.getName().contains(input)).map(E -> new ProtectedUser(E.getName(), E.getUsername(), E.getUserId())).toList();
    }


    class MessComp implements Comparator<Message> {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getTimeSent().compareTo(o2.getTimeSent());
        }
    }

    public List<Message> getUserMessages(EID user){
        List<Message> messList = new ArrayList<>(MessRepo.getStream().filter(E -> E.getReceiver().getUserId().equals(user) || E.getSender().getUserId().equals(user)).toList());
        messList.sort(new MessComp());
        return messList;
    }

    public void addMessage(Message mess) throws ElementExistsException {
        MessRepo.addElem(mess);
    }
}
