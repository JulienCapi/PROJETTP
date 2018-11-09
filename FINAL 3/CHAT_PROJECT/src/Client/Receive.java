package Client;

import Server.Message;
import Shared.Request;
import java.io.IOException;
import java.io.ObjectInputStream;

//recoit les données envoyee par le serveur
public class Receive implements Runnable {

    private ObjectInputStream in;
    private Object request;
    private Message message;
    private volatile boolean running = true;

    public Receive(ObjectInputStream in) {
        this.in = in;
    }

    public void terminate() { //permet de stopper le thread
        running = false;
    }

    public void run() {
        while (running) //Topicselection va interrompre ce thread si Transmit est interrompu, on stop alors l'xecution du run()
        {
            try {
                message = (Message) in.readObject();
                 if (message instanceof Message) {
                    if (message.getContent().equals("exit")) //si le client souhaite quitter le chat
                        terminate();
                    else
                        System.out.println(((Message) message).getAuthor() + " vous dit : " + ((Message) message).getContent()); //partir de la, on s'en fou de la requette, voir si je peux en envoyer plusieur avec la boucle de l'arraylist.
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
