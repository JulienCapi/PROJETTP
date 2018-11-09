package Server;

import java.io.Serializable;

public class Message implements Serializable {
    private String author;
    private String content;

    public Message(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
