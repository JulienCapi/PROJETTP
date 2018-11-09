package Client;

import Shared.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Connexion implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public static Thread ttopicselect;
    private boolean authflag = false;
    private String login;
    private String pass;
    private Request request;
    private Scanner sc;
    private String selection;

    public Connexion(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        sc = new Scanner(System.in);
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //la liaison avec le serveur est effectuee, il faut mainenant envoyer le login et password afin de s'autheitifier
        //on propose au client de se connecter ou de creer un compte

        do {
            System.out.println("Vous souhaitez :\n1 - Vous connecter\n2 - Creer un compte");
            selection = sc.next();
            if (!selection.equals("1") && (!selection.equals("2")))
                System.out.println("Reponse non valide");
        } while ((!selection.equals("1")) && (!selection.equals("2")));

        while (!authflag) { //tant que le client n'est pas authentifié
            try {
                request = (Request) in.readObject(); //recupere la requete de connection envoyee par le serveur
                if (request.equals(Request.LOGIN)) {
                    System.out.println("Veuillez entrer votre login : ");
                    login = sc.next(); //on recupere le login
                    out.writeObject(login); //puis on l'emvoi au serveur
                    out.flush();
                }
                request = (Request) in.readObject(); //on tente de recuere le requete de password
                if (request.equals(Request.PASS)) {
                    System.out.println("Veuillez entrer votre mot de passe : ");
                    pass = sc.next(); //on recupere le mot de passe
                    out.writeObject(pass); //puis on l'envoi au serveur
                    out.flush();
                }
                if (selection.equals("1")) { //si l'utilisateur souhaite se connecter
                    out.writeObject(Request.ACCOUNTLOGIN); //puis on l'envoi au serveur
                    out.flush();

                    request = (Request) in.readObject();

                    if (request.equals(Request.AUTHSUCCES)) {
                        System.out.println("Bonjour " + login); //a partir de la, le client devra choissir le salon qu'il souaite rejoindre
                        authflag = true;
                    } else if (request.equals(Request.AUTHFAILED)) {
                        System.err.println("Vos informations sont incorrectes ");
                    }
                }

                if (selection.equals("2")) { //si le client souhaite creer un compte
                    out.writeObject(Request.ACCOUNTCREATION); //puis on l'envoi au serveur
                    out.flush();
                    request = (Request) in.readObject();
                    if (request.equals(Request.ACCOUNTCREATIONSUCCES)) {
                        System.out.println("Votre compte a bien ete creer");
                        System.out.println("Connection...");
                        selection = "1";//on passe selection a 1 pour forcer les connection
                    } else if (request.equals(Request.ACCOUNTCREATIONFAIL)) {
                        System.out.println("Ce nom d'utilisateur existe deja, choisissez en un autre");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //On creer, puis on lance le thread de selection du topic
        ttopicselect = new Thread(new TopicSelection(in, out));
        ttopicselect.start();
    }
}
