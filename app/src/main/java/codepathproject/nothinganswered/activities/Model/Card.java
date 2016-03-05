package codepathproject.nothinganswered.activities.Model;

import java.util.ArrayList;


public class Card {

    private String mimageUrl;
    private String mimageTitle;

    private static int lastContactId = 0;


    public Card (String imageUrl, String imageTitle)
    {
        mimageTitle = imageTitle;
        mimageUrl = imageUrl;
    }

    public String getMimageUrl() {
        return mimageUrl;
    }

    public String getMimageTitle() {
        return mimageTitle;
    }


    public static ArrayList<Card> createCardsList(int numCards)
    {
        ArrayList<Card> card = new ArrayList<Card>();

        for (int i = 1; i <= numCards; i++) {
            card.add(new Card("1","3"));
        }

        return card;
    }


}
