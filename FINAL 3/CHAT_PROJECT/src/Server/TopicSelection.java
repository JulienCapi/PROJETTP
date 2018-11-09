package Server;

import Shared.Request;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TopicSelection implements Runnable {

    private ArrayList<ClientsConnection> clientsConnectionList;
    private Socket client;
    private int clientNumber;
    private Request response;
    private String topicName;
    private int topicID;
    private boolean flag = false;
    private ArrayList<Topic> topicsList;
    public static Thread t2;
    private Database db_pass;
    private Database db_topics;
    private String login;

    public TopicSelection(Socket client, ArrayList<ClientsConnection> clientsConnectionList, Database db_pass, Database db_topics) {
        this.client = client;
        this.clientsConnectionList = clientsConnectionList;
        this.db_pass = db_pass;
        this.db_topics = db_topics;
    }

    public void run() {
        try {
            this.topicsList = this.db_topics.loadTopics();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < clientsConnectionList.size(); i++) //on recupere le numero du client souhaitant se connecter
        {
            if (clientsConnectionList.get(i).getClient() == client)
                clientNumber = i;
        }

        do {
            try {
                response = null;
                response = (Request) clientsConnectionList.get(clientNumber).getIn().readObject(); //on recupere la reponse du client afin de savoir si il souhaite creer ou rejoindre un topic
                if (response.equals(Request.CREATETOPIC)) {
                    clientsConnectionList.get(clientNumber).getOut().writeObject("Quel nom souhaitez vous donner à votre salon ? :");
                    clientsConnectionList.get(clientNumber).getOut().flush();
                    topicName = (String) clientsConnectionList.get(clientNumber).getIn().readObject(); //on recupere le nom

                    if (isTopicNameAvailable(topicName, topicsList)) {
                        topicID = (topicsList.size() + 1); //on choisi un ID topic en fonction du nombre de topic present dans la liste
                        Topic topic = new Topic(topicName, topicID); // on creer un topic
                        System.out.println("topic topic : " + topic);
                        this.topicsList.add(topic); //on ajoute le topic a la liste
                        System.out.println("topiclist size + " + topicsList.size());
                        //mise ajour
                        db_topics.saveTopics(topicsList);
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.TOPICCREATIONSUCCES); //on averti le client que le topic a bien ete creer
                        clientsConnectionList.get(clientNumber).getOut().flush();
                    } else {
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.TOPICCREATIONFAIL); //on averti le client que le topic a bien ete creer
                    }

                } else if (response.equals(Request.JOINTOPIC)) {
                    clientsConnectionList.get(clientNumber).getOut().writeObject(db_topics.loadTopics()); // on envoi au client la liste des topics
                    clientsConnectionList.get(clientNumber).getOut().flush();

                    if (topicsList.size() > 0) { //si il n'y a aucun topic de creer, on propse a l'utilisateur d'en creer en
                        topicID = (int) clientsConnectionList.get(clientNumber).getIn().readObject(); //on recupere le numero du topic a rejoindre

                        clientsConnectionList.get(clientNumber).setTopicID(topicID); //on inscrit le numero du topic dans les informations clients
                        //Lorsque qu'un client enveera un message, seul les autres client connectes portant le meme nemero de topic pourron le recevoir
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.TOPICJOINSUCCES); //on indique au client qu'il a bien rejoin le topic
                        clientsConnectionList.get(clientNumber).getOut().flush();
                        flag = true;
                        t2 = new Thread(new Chat(client, clientsConnectionList, db_pass, db_topics)); //on demarre le chat
                        t2.start();
                    }
                } else if (response.equals(Request.EXIT)) {
                    login = clientsConnectionList.get(clientNumber).getLogin();
                    clientsConnectionList.get(clientNumber).Disconnect(); // on deconnecte le client
                    clientsConnectionList.remove(clientNumber); //puis on le supprime de la liste
                    System.out.println(login + " viens de de déconnecter. Il reste actuellement " + clientsConnectionList.size() + " client(s) connecté(s)");
                    flag = true; //pour ne plus afficher le menu
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } while (!flag); //tant le chat n'est pas lancé, on reaffiche le menu principal
    }

    private static boolean isTopicNameAvailable(String topicName, ArrayList<Topic> topicList) {
        boolean available = true;

        for (int i = 0; i < topicList.size(); i++) //on parcours tout l'arraylist, si un topic du meme nom est deja present, on renvoi false, true sinon
        {
            if ((topicName.equals(topicList.get(i).getTopicSubject())))
                available = false;
        }
        return available;
    }
}
