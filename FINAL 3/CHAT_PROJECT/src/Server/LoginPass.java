package Server;

import java.io.Serializable;

public class LoginPass implements Serializable {

    private String login;
    private String pass;

    public LoginPass(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }
}
