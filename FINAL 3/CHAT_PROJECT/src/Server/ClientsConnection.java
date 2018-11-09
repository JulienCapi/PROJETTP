package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Permet de creer les flux d' i/o de chaque client connecte a partir de leur socket

public class ClientsConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String login = null; // le login est null jusqu'a ce que la personne soit authentifiee
    private boolean connected = false;
    private boolean auth;
    private int topicID = 0; //idique a quel topic le client appartient, lorsqu'il quite un topic, doit etre remis a 0

    public ClientsConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        connected = true;
    }

    public Socket getClient() {
        return socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public int getTopicID() {
        return topicID;
    }

    public Socket getSocket() {
        return socket;
    }

    public void Disconnect() throws IOException {
        out.close();
        in.close();
        socket.close();
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
