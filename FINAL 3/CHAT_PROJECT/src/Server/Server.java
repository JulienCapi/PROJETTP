package Server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static ServerSocket ss = null;
    public static Thread t;
    static Database db_pass;
    static Database db_topics;
    public static final String DB_PASS_FILE_NAME = "passwords.db";
    public static final String DB_TOPICS_FILE_NAME = "topics.db";

    public static void main(String[] args) {

        try {
            ss = new ServerSocket(3000);
            System.out.println("Le serveur est à l'écoute du port " + ss.getLocalPort());

            //instanciation de sbases de donnes
            db_pass = new Database(DB_PASS_FILE_NAME); // on creer la base de donnee de longin/passwords
            db_topics = new Database(DB_TOPICS_FILE_NAME); // on creer la base de donnee de longin/passwords

            t = new Thread(new Handle_Client(ss, db_pass, db_topics));
            t.start();

        } catch (IOException e) {
            System.err.println("Le port " + ss.getLocalPort() + " est déjà utilisé !");
        }
    }
}