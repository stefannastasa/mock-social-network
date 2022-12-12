package Entities;

public class ProtectedUser {

    private String username, name;
    private EID user_id;

    public ProtectedUser(String username, String name, EID user_id) {
        this.username = username;
        this.name = name;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EID getUser_id() {
        return user_id;
    }
}
