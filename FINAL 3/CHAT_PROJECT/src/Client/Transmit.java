package Client;

import Shared.Request;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Transmit implements Runnable {

    private Scanner sc;
    private ObjectOutputStream out;
    private volatile boolean running = true;

    public Transmit(ObjectOutputStream out) {
        this.out = out;
    }

    public void terminate() {
        running = false;
    }

    public void run() {
        sc = new Scanner(System.in);
        System.out.println("Vous pouvez maintenant discuter avec les autre membres du chat");
        while (running) {
            String message = sc.nextLine();

            try {
                if (message.equals("exit")) //si le client souhaite revenir au menu proincipal
                {
                    out.writeObject(Request.BACKMENU);
                    out.flush();
                    System.out.println("A bientot sur le chat!");
                    terminate();
                } else {
                    out.writeObject(Request.MESSAGE);
                    out.writeObject(message);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
