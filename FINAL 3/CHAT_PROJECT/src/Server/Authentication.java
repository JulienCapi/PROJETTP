package Server;

import Shared.Request;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Authentication implements Runnable {

    private Scanner sc;
    private Socket client;
    private int clientNumber;
    private Object login;
    private Object pass;
    private boolean authflag = false;
    public static Thread t2;
    private Request request;
    private LoginPass loginpass;
    private ArrayList<LoginPass> loginsList;
    private Database db_pass;
    private Database db_topics;

    private ArrayList<ClientsConnection> clientsConnectionList;

    public Authentication(Socket client, ArrayList<ClientsConnection> clientsConnectionList, Database db_pass, Database db_topics) {
        this.client = client;
        this.clientsConnectionList = clientsConnectionList;
        this.db_pass = db_pass;
        this.db_topics = db_topics;
    }

    public void run() {
        try {
            this.loginsList = this.db_pass.loadLogins(); //on charge la base
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        sc = new Scanner(System.in);

        for (int i = 0; i < clientsConnectionList.size(); i++) //on recupere le numero du client souhaitant se connecter
        {
            if (clientsConnectionList.get(i).getClient() == client)
                clientNumber = i;
        }

        //CHOIX CONCERNANT LA CONNEXION ET LA CRETION DE COMPTE


        while (!authflag) { //tant que le client n'est pas authentifié
            try {
                clientsConnectionList.get(clientNumber).getOut().writeObject(Request.LOGIN);
                clientsConnectionList.get(clientNumber).getOut().flush();
                try {
                    this.login = clientsConnectionList.get(clientNumber).getIn().readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                clientsConnectionList.get(clientNumber).getOut().writeObject(Request.PASS);
                clientsConnectionList.get(clientNumber).getOut().flush();
                this.pass = clientsConnectionList.get(clientNumber).getIn().readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                request = (Request) clientsConnectionList.get(clientNumber).getIn().readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (request.equals(Request.ACCOUNTCREATION)) {
                if (isAvailable((String) login, loginsList)) { //on verifie si ce login existe deja
                    loginpass = new LoginPass((String) login, (String) pass); //on creer un object logn pass
                    loginsList.add(loginpass); //on l'ajoute a la liste
                    try {
                        db_pass.saveLogins(loginsList);
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.ACCOUNTCREATIONSUCCES);
                        clientsConnectionList.get(clientNumber).getOut().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.ACCOUNTCREATIONFAIL);
                        clientsConnectionList.get(clientNumber).getOut().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (request.equals(Request.ACCOUNTLOGIN)) {
                if (isValid((String) login, (String) pass, loginsList)) { //si login/pass match
                    try {
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.AUTHSUCCES);
                        clientsConnectionList.get(clientNumber).getOut().flush();
                        System.out.println(login + " vient de se connecter ");
                        authflag = true;
                        clientsConnectionList.get(clientNumber).setAuth(true); //on indique a notre liste que l'utilisateur est bien authentifie

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else { // si les infos sont erronnes
                    try {
                        clientsConnectionList.get(clientNumber).getOut().writeObject(Request.AUTHFAILED);
                        clientsConnectionList.get(clientNumber).getOut().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        clientsConnectionList.get(clientNumber).setLogin((String) login); //on ajoute le login du client pour permettre son identification dans le chat
        clientsConnectionList.get(clientNumber).setAuth(true); //on indique que ce clien fais parti de client authentifiés

        //on demarre le threaf de selection du topic
        t2 = new Thread(new TopicSelection(client, clientsConnectionList, db_pass, db_topics)); //selelction du topic
        t2.start();
    }

    private static boolean isValid(String login, String pass, ArrayList<LoginPass> logins) {
        boolean connexion = false;

        for (int i = 0; i < logins.size(); i++) //on parcours tout l'arraylist, si un login et pass match on renvoi true, false sinon
        {
            if ((login.equals(logins.get(i).getLogin())) && (pass.equals(logins.get(i).getPass()))) {
                connexion = true;
            }
        }
        return connexion;
    }

    private static boolean isAvailable(String login, ArrayList<LoginPass> logins) {

        boolean available = true;

        for (int i = 0; i < logins.size(); i++) //on parcours tout l'arraylist, si un login est deja present, on renvoi false, true sinon
        {
            if ((login.equals(logins.get(i).getLogin())))
                available = false;
        }
        return available;
    }
}
