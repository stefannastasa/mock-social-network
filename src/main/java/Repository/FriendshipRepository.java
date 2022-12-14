package Repository;

import Entities.Friendships;
import Exceptions.CustomException;
import Exceptions.RepoSpecific.ElementExistsException;
import Exceptions.RepoSpecific.ElementNotFoundException;
import Entities.EID;
import Exceptions.SocialNetworkException;
import Utils.FriendshipStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.stream.Stream;

public class FriendshipRepository implements Repository<EID, Friendships> {
    private final HashMap<EID, Friendships> elemList = new HashMap<>();
    private final String url = "jdbc:postgresql://localhost:5432/mock-social-network";

    private void changeFromDb(EID user1, EID user2, LocalDateTime new_date, FriendshipStatus new_status){
        String sql = "UPDATE FRIENDSHIPS SET friendsSince = ?, status = ? WHERE user1 = ? AND user2 = ? ";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement st = connection.prepareStatement(sql);) {

            st.setObject(1, new_date);
            st.setString(2,new_status.toString());
            st.setBytes(3, user1.getRaw());
            st.setBytes(4, user2.getRaw());

            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void addToDb(EID user1, EID user2, LocalDateTime friendsSince, FriendshipStatus status){
        String sql = "INSERT INTO FRIENDSHIPS (user1, user2, friendsSince, status) VALUES (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement st = connection.prepareStatement(sql);) {

            st.setBytes(1, user1.getRaw());
            st.setBytes(2, user2.getRaw());
            st.setObject(3, friendsSince);
            st.setString(4, status.toString());


            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void deleteFromDb(EID user1, EID user2){
        String sql = "DELETE FROM FRIENDSHIPS WHERE user1=? AND user2=?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement st = connection.prepareStatement(sql)) {
            st.setBytes(1,user1.getRaw());
            st.setBytes(2, user2.getRaw());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FriendshipRepository(){
        // fetches all friendships from the database
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM FRIENDSHIPS");
             ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next()){
                EID user1 = new EID(resultSet.getBytes("user1"));
                EID user2 = new EID(resultSet.getBytes("user2"));
                LocalDateTime friendsSince = LocalDateTime.from(resultSet.getTimestamp("friendssince").toLocalDateTime());
                String sstatus = resultSet.getString("status");
                FriendshipStatus real_status;
                if(sstatus.equals("PENDING"))
                    real_status = FriendshipStatus.PENDING;
                else if (sstatus.equals("SENT"))
                    real_status = FriendshipStatus.SENT;
                else
                    real_status = FriendshipStatus.FRIENDS;

                try{
                    if(!this.elemList.containsKey(user1)){
                        Friendships Fs = new Friendships(user1);
                        this.elemList.put(Fs.getUserId(), Fs);
                    }
                    this.lookUp(user1).addFriend(user2, friendsSince, real_status);


                } catch (ElementExistsException e) {
                    e.printStackTrace();
                }


            }
        } catch (SQLException | SocialNetworkException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addElem(Friendships Fs) throws ElementExistsException {
        if(this.elemList.containsKey(Fs.getUserId()))
            throw new ElementExistsException();

        this.elemList.put(Fs.getUserId(), Fs);
    }


    public void addFriends(EID keyA, EID keyB) throws SocialNetworkException {
        if(this.lookUp(keyA) == null){
            this.addElem(new Friendships(keyA));
        }
        if(this.lookUp(keyB) == null){
            this.addElem(new Friendships(keyB));
        }
        this.lookUp(keyA).addFriend(keyB);
        this.lookUp(keyA).changeFriend(keyB, LocalDateTime.now(), FriendshipStatus.SENT);
        this.lookUp(keyB).addFriend(keyA);
        this.lookUp(keyB).changeFriend(keyA, LocalDateTime.now(), FriendshipStatus.PENDING);

        addToDb(keyA, keyB, LocalDateTime.now(), FriendshipStatus.SENT);
        addToDb(keyB, keyA, LocalDateTime.now(), FriendshipStatus.PENDING);
    }

    public void updateFriendship(EID user1, EID user2, LocalDateTime new_date, FriendshipStatus new_status) throws SocialNetworkException {
        if( this.lookUp(user1) == null || this.lookUp(user2) == null){
            throw new CustomException("Friendship is not valid");
        }
        if(new_date == null){
            new_date = this.lookUp(user1).getFriendshipProperties(user2).getDateTime();
        }
        if( new_status == null){
            new_status = this.lookUp(user1).getFriendshipProperties(user2).getStatus();
        }


        this.lookUp(user1).changeFriend(user2, new_date, new_status);
        changeFromDb(user1, user2, new_date, new_status);

    }
    public void removeFriends(EID keyA, EID keyB) throws SocialNetworkException {
        this.lookUp(keyA).removeFriend(keyB);
        this.lookUp(keyB).removeFriend(keyA);
        deleteFromDb(keyA, keyB);
        deleteFromDb(keyB, keyA);
    }

    @Override
    public void removeElem(EID key) throws ElementNotFoundException {
        if(!elemList.containsKey(key))
            throw new ElementNotFoundException();

        elemList.remove(key);
    }

    @Override
    public Friendships lookUp(EID key) {
        return elemList.get(key);
    }

    @Override
    public Stream<Friendships> getStream() {
        return elemList.values().stream();
    }


    @Override
    public boolean isEmpty() {
        return elemList.isEmpty();
    }

    @Override
    public int size() {
        return elemList.size();
    }
}
