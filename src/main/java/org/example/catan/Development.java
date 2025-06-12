package org.example.catan;

//Development cards
public class Development {
    private String name;
    private String cardText;

    public Development(String name, String cardText) {
        this.name = name;
        this.cardText = cardText;
    }


    public String getName() {
        return name;
    }

    public String getText() {
        return cardText;
    }

    public String toString() {
        return name + ": " + cardText;
    }
}
