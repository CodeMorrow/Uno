/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interfaceschool.game.uno;

import com.interfaceschool.game.Card;
import com.interfaceschool.game.Rank;
import com.interfaceschool.game.Suit;

/**
 *
 * @author morrow
 */
public class UnoCard extends Card {
    public UnoCard(Rank rank, Suit suit) {
        super(rank, suit);
    }
    
    public boolean isWild() {
        return getRank().getText().startsWith("W");
    }

    @Override
    public String toString() {
        return getSuit().toString() + getRank().toString();
    }
}
