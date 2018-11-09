package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Handle_Client implements Runnable {
    private ServerSocket socketserver;
    private Socket client;
    protected ArrayList<ClientsConnection> clientsConnectionList;
    private ClientsConnection cc;
    public static Thread t1;
    public static Thread t2;
    private Database db_pass;
    private Database db_topics;

    public Handle_Client(ServerSocket ss, Database db_pass, Database db_topics) {
        socketserver = ss;
        this.db_pass = db_pass;
        this.db_topics = db_topics;
    }

    public void run() {

        clientsConnectionList = new ArrayList<ClientsConnection>();
        try {
            while (true) {

                System.out.println("En attente de connexion...");
                client = socketserver.accept();
                cc = new ClientsConnection(client); //on creer l'object ClientConnection a partir de la socket
                clientsConnectionList.add(cc); //on ajoute le client+les flux a la liste, on pourra ensuite broadcaster des message grace a cette liste
                System.out.println("Un client s'est connecté. " + clientsConnectionList.size() + " client(s) sont actuellement connectés");
                t1 = new Thread(new Authentication(client, clientsConnectionList, db_pass, db_topics));
                t1.start();
            }
        } catch (IOException e) {

            System.err.println("Erreur serveur");
        }
    }
}
