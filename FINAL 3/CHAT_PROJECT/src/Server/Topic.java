package Server;

import java.io.Serializable;
import java.util.ArrayList;

public class Topic implements Serializable {

    private String topicSubject;
    private int topicID;
    private ArrayList<Message> messageListInTopic;

    public Topic(String topicSubject, int topicID) {
        this.topicSubject = topicSubject;
        this.topicID = topicID;
        this.messageListInTopic = new ArrayList<Message>();

    }

    public void addMessageToList(Message message) { //on ajoute le message au topic associe
        this.messageListInTopic.add(message);
    }

    public ArrayList<Message> getMessageListInTopic() { //charger aussi
        return messageListInTopic;
    }

    public int getTopicID() {
        return topicID;
    }

    public String getTopicSubject() {
        return topicSubject;
    }
}
