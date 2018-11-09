package Client;

import Server.Message;
import Server.Topic;
import Shared.Request;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class TopicSelection implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Request request;
    private Scanner sc;
    private String topicName;
    private ArrayList<Topic> topicList;
    private ArrayList<Message> messageList;
    private String topicID_t;
    private int topicID;
    public static Thread ttransmit;
    public static Thread treceive;
    private boolean flag = false;
    private Transmit runnablet = null;
    private Receive runnabler = null;
    private boolean numberFormatOk = false;

    public TopicSelection(ObjectInputStream in, ObjectOutputStream out) {
        this.out = out;
        this.in = in;
    }

    public void run() {
        sc = new Scanner(System.in);
        messageList = new ArrayList<Message>();
        do {
            System.out.println("Vous souhaitez :\n1 - Joindre un salon\n2 - Creer un salon\n3 - Quitter");
            String choice = sc.next();

            if (choice.equals("1")) {
                try {
                    out.writeObject(Request.JOINTOPIC); //on envoi la  requette correspndant a la reponse du client
                    out.flush();
                    topicList = (ArrayList<Topic>) in.readObject(); // PB //on recupere la liste des salon envoyee par le serveur //PB

                    if (topicList.size() == 0) {
                        System.out.println("Aucun topic. Vous pouvez en creer un maintenant");
                    } else {
                        do {
                            System.out.println("Voici la liste des salons disponible");
                            DisplayTopics(topicList); //on affiche les topics
                            System.out.println("Quel est le numero du salon que vous souhaitez rejoindre ?");
                            topicID_t = sc.next();

                            if (topicID_t.matches("[0-9]+")) { //si la chaine ne contient que des chiffres
                                topicID = Integer.valueOf(topicID_t); //n la convertie en entier pour pouvoir la comparer ensuite
                                if (topicID < 1 || topicID > topicList.size()) {
                                    System.out.println("Réponse invalide");
                                } else {
                                    numberFormatOk = true;
                                }
                            } else {
                                System.out.println("Réponse invalide. Veuillez entrer uniquement des chiffres");
                            }
                        }
                        while (!numberFormatOk); //tant que ce que rentre l'utilise n'est pas un chiffre compris dans la bonne plage, on reboucle

                        messageList = topicList.get(topicID - 1).getMessageListInTopic();
                        System.out.println(messageList);
                        out.writeObject(topicID); //on envoi le choix du client concernant le numero du salon a rejoindre
                        out.flush();
                        request = (Request) in.readObject();
                        if (request.equals(Request.TOPICJOINSUCCES)) {
                            System.out.println("Vous avez bien rejoin le topic " + topicList.get(topicID - 1).getTopicSubject());
                            flag = true; //on passe le flag a true pour arreter d'affichier le menu
                            for (int i = 0; i < messageList.size(); i++) {
                                System.out.println(messageList.get(i).getAuthor() + " : " + messageList.get(i).getContent());
                            }
                        }

                        //On creer et on lance les threads d'emission reception
                        runnablet = new Transmit(out);
                        ttransmit = new Thread(runnablet);
                        ttransmit.start();
                        runnabler = new Receive(in);
                        treceive = new Thread(runnabler);
                        treceive.start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("2")) {
                try {
                    do {
                        out.writeObject(Request.CREATETOPIC);
                        out.flush();
                        System.out.println((String) in.readObject()); //PB
                        topicName = sc.next();
                        out.writeObject(topicName);
                        out.flush();
                        request = (Request) in.readObject();
                        if (request.equals(Request.TOPICCREATIONSUCCES)) {
                            System.out.println("Topic creer avec succès, vous pouvez desormais y acceder");
                            System.out.println("Retour au menu principal...");
                        } else if (request.equals(Request.TOPICCREATIONFAIL)) {
                            System.out.println("Ce nom de topic existe déja, choisissez en un autre");
                        }
                    } while (request.equals(Request.TOPICCREATIONFAIL));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (choice.equals("3")) {
                try {
                    out.writeObject(Request.EXIT); //on envoi au serveur la requette pour se deconnecter
                    out.flush();
                    System.out.println("A bientot sur le chat!");
                    flag = true; //on passe le flag a true pour arreter d'affichier le menu
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Reponse non valide");
            }
        } while (!flag);
    }

    private static void DisplayTopics(ArrayList<Topic> topicList) {
        for (int i = 0; i < topicList.size(); i++) {
            System.out.println("Salon " + topicList.get(i).getTopicID() + " : Nom : " + topicList.get(i).getTopicSubject());
        }
    }
}

