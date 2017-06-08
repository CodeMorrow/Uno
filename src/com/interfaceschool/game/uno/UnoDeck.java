/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interfaceschool.game.uno;

import com.interfaceschool.game.Card;
import com.interfaceschool.game.CardGroup;
import com.interfaceschool.game.Rank;
import com.interfaceschool.game.Suit;
import static com.interfaceschool.game.uno.Uno.BLACK_TEXT;
import static com.interfaceschool.game.uno.Uno.BLUE_BG;
import static com.interfaceschool.game.uno.Uno.GREEN_BG;
import static com.interfaceschool.game.uno.Uno.PURPLE_BG;
import static com.interfaceschool.game.uno.Uno.RED_BG;
import static com.interfaceschool.game.uno.Uno.WHITE_TEXT;
import static com.interfaceschool.game.uno.Uno.YELLOW_BG;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author morrow
 */
public class UnoDeck extends CardGroup {

    public static final Map<String, String> suitMap = new HashMap<>();
    public static final String[] SUITS = {PURPLE_BG + WHITE_TEXT, RED_BG + WHITE_TEXT, YELLOW_BG + BLACK_TEXT, GREEN_BG + BLACK_TEXT, BLUE_BG + WHITE_TEXT}; // WILD, Red, Yellow, Green, Blue
    public static final String[] RANKS = {"W", "W4", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "R", "D2", "S"};

    static {
        setSuits();
    }
    private static void setSuits() {
        suitMap.put("wild", PURPLE_BG + WHITE_TEXT);
        suitMap.put("red", RED_BG + WHITE_TEXT);
        suitMap.put("yellow", YELLOW_BG + BLACK_TEXT);
        suitMap.put("green", GREEN_BG + BLACK_TEXT);
        suitMap.put("blue", BLUE_BG + WHITE_TEXT);
    }
    
    public UnoDeck() {
        for (String suitName: suitMap.keySet()) {
            Suit suit = new Suit(0, suitMap.get(suitName));
            for (int rankValue = 0; rankValue < RANKS.length; rankValue++) {
                Rank rank = new Rank(rankValue, RANKS[rankValue]);
                Card card = new UnoCard(rank, suit);
                if (suitName.equals("wild") && rankValue < 2) {
                    for (int idx = 0; idx < 4; idx++) {
                        card = new UnoCard(rank, suit);
                        suit = new Suit(0, suitMap.get(suitName));
                        addCard(card);
                    }
                } else if (!suitName.equals("wild") && rankValue >= 2) {
                    addCard(card);
                    if (rankValue > 2) {
                        addCard(card);
                    }
                }
            }
        }
    }
}
