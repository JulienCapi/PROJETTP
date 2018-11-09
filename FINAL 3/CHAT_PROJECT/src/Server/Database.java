package Server;

import java.io.*;
import java.util.ArrayList;

//Cette classe a ete copiee en quasi integralite du projet pokemon
public class Database {
    private File file;

    public Database(String fileName) {
        this.file = new File(fileName);
    }


    public ArrayList<Topic> loadTopics() throws IOException, ClassNotFoundException {
        ArrayList<Topic> data = new ArrayList<Topic>();

        // On verifie si le fichier existe
        if (this.file.exists() && !this.file.isDirectory()) {

            FileInputStream fis = new FileInputStream(this.file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (ArrayList<Topic>) ois.readObject();
            ois.close();
        } else {
            System.out.println("Le fichier de sauvegarde n'existe pas.");
        }

        System.out.println(data.size() + " Topic(s) chargé(s) depuis la sauvegarde.");
        return data;
    }

    public void saveTopics(ArrayList<Topic> data) throws IOException {

        FileOutputStream fos = new FileOutputStream(this.file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        oos.close();
        System.out.println("Sauvegarde effectuée... " + data.size() + " Topic(s) dans la base de données.");
    }

    public ArrayList<LoginPass> loadLogins() throws IOException, ClassNotFoundException {
        ArrayList<LoginPass> data = new ArrayList<LoginPass>();

        // On verifie si le fichier existe
        if (this.file.exists() && !this.file.isDirectory()) {

            FileInputStream fis = new FileInputStream(this.file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (ArrayList<LoginPass>) ois.readObject();
            ois.close();
        } else {
            System.out.println("Le fichier de sauvegarde n'existe pas.");
        }

        System.out.println(data.size() + " Comptes(s) chargé(s) depuis la sauvegarde.");
        return data;
    }

    public void saveLogins(ArrayList<LoginPass> data) throws IOException {

        FileOutputStream fos = new FileOutputStream(this.file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        oos.close();
        System.out.println("Sauvegarde effectuée... " + data.size() + " Compte(s) dans la base de données.");
    }
}

