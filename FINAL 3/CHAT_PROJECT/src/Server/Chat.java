package Server;

import Shared.Request;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Chat implements Runnable {
    private Socket client;
    private int clientNumber = 0;
    private ArrayList<ClientsConnection> clientsConnectionList;
    private Message messageDetailed;
    private volatile ArrayList<Topic> topicList;
    private volatile boolean running = true;
    private Database db_pass;
    private Database db_topics;

    public Chat(Socket client, ArrayList<ClientsConnection> clientsConnectionList, Database db_pass, Database db_topics) {
        this.client = client;
        this.clientsConnectionList = clientsConnectionList;
        this.db_pass = db_pass;
        this.db_topics = db_topics;
    }

    public void terminate() {
        running = false;
    }

    public void run() {
        try {
            topicList = db_topics.loadTopics(); //on recuper les topics a paetir de la base de donnes

            for (int i = 0; i < clientsConnectionList.size(); i++) {
                if (clientsConnectionList.get(i).getClient() == client)
                    clientNumber = i;
            }

            while (clientsConnectionList.get(clientNumber).isConnected()) { //tester avec isAlive
                Request request = (Request) clientsConnectionList.get(clientNumber).getIn().readObject();

                if (request.equals(Request.MESSAGE)) {
                    topicList = db_topics.loadTopics(); //on recuper les topics a paetir de la base de donnes
                    System.out.print("Message du client " + (clientsConnectionList.get(clientNumber).getLogin()) + " :"); //on ajoute 1 simplement pour l'affichage
                    String message = (String) clientsConnectionList.get(clientNumber).getIn().readObject(); //on recupere le message
                    System.out.println(message);

                    messageDetailed = new Message(message, clientsConnectionList.get(clientNumber).getLogin()); // on ajoute le nom de l'emetteur
                    topicList.get(clientsConnectionList.get(clientNumber).getTopicID() - 1).addMessageToList(messageDetailed); //on ajoute le message envoye a la liste de messages concerne par ce topic
                    db_topics.saveTopics(topicList); //on sauvegarde la base

                    for (int i = 0; i < clientsConnectionList.size(); i++) { //on envoi le message recu à tous les clients connectés, sauf à l'emetteur. Les message sont transmis uniquement
                        //aux client appartenant au meme topic

                        //si le client destinataire porte le meme topic id et ai connecte, alors on lui transmet le message
                        if ((i != clientNumber) && (clientsConnectionList.get(i).getTopicID() == clientsConnectionList.get(clientNumber).getTopicID()) && clientsConnectionList.get(i).isConnected()) {
                            clientsConnectionList.get(i).getOut().writeObject(messageDetailed);
                        }
                    }
                } else if (request.equals(Request.BACKMENU)) {
                    clientsConnectionList.get(clientNumber).getOut().writeObject(new Message("exit", "server")); //on renvoi back menu pour le thread receive puisse egalemet s'interrompre
                    System.out.println(clientsConnectionList.get(clientNumber).getLogin() + " s'est déconnecté");
                    System.out.println("Il reste " + (getNClientsConnected() - 1) + " clients connectés");
                    //db_topics.saveTopics(topicList); //si le client se deconnecte, on sauvegarde les messages,
                    terminate(); //on arrete d'executer le run()
                    clientsConnectionList.get(clientNumber).Disconnect(); //on enleve le client a la liste des cleitn connectes
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getNClientsConnected() {
        int nclientsConnected = 0;
        for (int i = 0; i < clientsConnectionList.size(); i++) {
            if (clientsConnectionList.get(i).isConnected())
                nclientsConnected++;
        }
        return nclientsConnected;
    }
}


