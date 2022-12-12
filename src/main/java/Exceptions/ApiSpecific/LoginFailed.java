package Exceptions.ApiSpecific;

import Exceptions.SocialNetworkException;

public class LoginFailed extends SocialNetworkException {
    public LoginFailed() {
        super("User login failed.");
    }
}
