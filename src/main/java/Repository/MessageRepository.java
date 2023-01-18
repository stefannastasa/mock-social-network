package Repository;

import Entities.EID;
import Entities.Friendships;
import Entities.Message;
import Exceptions.RepoSpecific.ElementExistsException;
import Exceptions.RepoSpecific.ElementNotFoundException;
import Exceptions.SocialNetworkException;
import Utils.FriendshipStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MessageRepository{
    private List<Message> messageList = new ArrayList<>();
    private UserRepository users = new UserRepository();
    private final String url = "jdbc:postgresql://localhost:5432/mock-social-network";

    public MessageRepository(){
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM MESSAGES");
             ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next()){
                EID sender = new EID(resultSet.getBytes("sender"));
                EID receiver = new EID(resultSet.getBytes("receiver"));
                LocalDateTime sentTime = LocalDateTime.from(resultSet.getTimestamp("time").toLocalDateTime());
                String text = resultSet.getString("text");
                messageList.add(new Message(users.lookUp(sender), users.lookUp(receiver),
                                    text, sentTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addElem(Message M) throws ElementExistsException {
        messageList.add(M);
        String sql = "INSERT INTO MESSAGES (sender, receiver, text, time) VALUES (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement st = connection.prepareStatement(sql);) {
            st.setBytes(1, M.getSender().getUserId().getRaw());
            st.setBytes(2, M.getReceiver().getUserId().getRaw());
            st.setString(3, M.getMessage());
            st.setObject(4, M.getTimeSent());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<Message> getStream() {
        return messageList.stream();
    }

    public boolean isEmpty() {
        return messageList.isEmpty();
    }

    public int size() {
        return messageList.size();
    }

}
