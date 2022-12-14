package Entities;

import Exceptions.CustomException;
import Exceptions.SocialNetworkException;
import Utils.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Class created to manage the friendships of a certain user.
 * */
public class Friendships {

    private EID userId;

    /* If we want to add any properties to the friendship, we should replace LocalDatetime
    * with a custom class called properties */
    private final HashSet< Friendship >friendships = new HashSet<>();

    /**
     * Constructor.
     * @param U EID of the User whose friendships these are
     * */
    public Friendships(EID U){
        this.userId = U;
    }

    /**
     * Return the EID of the User whose friendships these are.
     * @return EID id of the user
     * */
    public EID getUserId() {
        return userId;
    }

    /**
     * Setter for userId
     * @param userId the new id of the user
     * */
    public void setUserId(EID userId) {
        this.userId = userId;
    }

    /**
     * Adds a new friend to the friend list of the user.
     * @param F id of the new friend.
     * @throws SocialNetworkException in case of the friendship already existing
     * */
    public void addFriend(EID F) throws SocialNetworkException {
        if(!friendships.isEmpty()){
            boolean err = friendships.stream().anyMatch(E -> E.getUser().equals(F));
            if(err)
                throw new CustomException("Users are already friends!");
        }
        friendships.add(new Friendship(LocalDateTime.now(), FriendshipStatus.PENDING, F));

    }

    public void addFriend(EID F, LocalDateTime friendsSince, FriendshipStatus status) throws SocialNetworkException{
        if(!friendships.isEmpty()){
            boolean err = friendships.stream().anyMatch(E -> E.getUser().equals(F));
            if(err)
                throw new CustomException("Users are already friends!");
        }
        friendships.add(new Friendship(friendsSince, status, F));
    }
    /**
     * Removes the friend from the friendship list of the user.
     *
     * @param F id of the friend to be removed
     * @throws SocialNetworkException in case of there not being a friendship between the users beforehand */
    public void removeFriend(EID F) throws SocialNetworkException {
        boolean err = friendships.removeIf(E -> E.getUser().equals(F));
        if(!err)
            throw new CustomException("Users were not friends!");
    }

    public void changeFriend(EID F, LocalDateTime new_date, FriendshipStatus new_status) throws SocialNetworkException {
        boolean err = friendships.stream().anyMatch(E -> E.getUser().equals(F));
        if(!err){
            throw new CustomException("No friendship found.");
        }
        friendships.stream().filter(E -> E.getUser().equals(F))
                    .forEach(
                            E -> {
                                E.setStatus(new_status);
                                E.setDateTime(new_date);
                            }
                    );
    }
    public Friendship getFriendshipProperties(EID F){
        return friendships.stream().filter(E -> E.getUser().equals(F)).toList().get(0);
    }
    /**
     * Checks if given user is a friend of the user.
     * @param F id of the friend
     * @return True if F is a friend, else false
     * */
    public boolean isFriend(EID F){
        return friendships.stream().anyMatch(E -> E.getUser().equals(F));
    }

    /**
     * Return a stream for easier data browsing.
     * @return Stream of all the friends of the user.
     * */
    public Stream<Friendship> getStream(){
        return friendships.stream();
    }
    /**
     * Checks if the user has any friends =(.
     * @return  True if the user has friends.
     * */
    public boolean hasFriends(){
        return !friendships.isEmpty();
    }

    /**
     * Gathers the number of friends the user has.
     * @return int the number of friends the user has
     * */
    public int nrOfFriends(){
        return friendships.size();
    }
}
