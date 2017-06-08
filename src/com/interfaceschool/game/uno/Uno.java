/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interfaceschool.game.uno;

import com.interfaceschool.game.Card;
import com.interfaceschool.game.CardGroup;
import com.interfaceschool.game.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author morrow
 */
public class Uno {

    protected List<Player> players = new LinkedList<>();
    protected UnoDeck deck;
    protected CardGroup discardPile;
    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    // Start with the player index -1 (a fake player) so that the initial playCard works
    int currentPlayerIndex = -1;
    int directionOfPlay = 1; //-1 for reverse
    int startingPlayerIndex = 0;
    Player currentPlayer;

    public static final String RESET_TEXT = "\u001B[0m";
    public static final String BLACK_TEXT = "\u001B[30m";
    public static final String WHITE_TEXT = "\u001B[37m";

    public static final String RED_BG = "\u001B[41m";
    public static final String GREEN_BG = "\u001B[42m";
    public static final String YELLOW_BG = "\u001B[43m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String PURPLE_BG = "\u001B[45m";

    public static void main(String[] args) throws IOException {
        Uno game = new Uno();
        game.setupGame();
        game.currentPlayer = game.players.get(game.currentPlayerIndex);
        while (!game.takeTurn()) {
            game.currentPlayer = game.players.get(game.currentPlayerIndex);
        }
    }

    private void setupGame() throws IOException {
        promptForPlayers();
        currentPlayer = players.get(startingPlayerIndex);
        deck = new UnoDeck();
        deck.shuffleCards();
        dealCards();
        discardPile = new CardGroup();
        Card card = deck.removeLastCard();
        while (card.isWild()){
            deck.addCard(card);
            deck.shuffleCards();
            card = deck.removeLastCard();
        }
        playCard(card, getCardSuitName(card));
        currentPlayer = players.get(currentPlayerIndex);
    }

    private void promptForPlayers() throws IOException {
        while (true) {
            System.out.println("Enter Player Name");
            String name = console.readLine().trim();
            if (name.equals("")) {
                break;
            }
            if (players.contains(name)) {
                System.out.println(name + " has already been take.\nPlease a unique player name");
                continue;
            }
            Player player = new Player(name);
            players.add(player);
        }
    }

    private void dealCards() {
        for (int count = 0; count < 7; count++) {
            for (Player player : players) {
                Card card = deck.removeLastCard();
                player.getHand().addCard(card);
            }
        }
    }

    private String promptPlayerTurn() throws IOException {
        System.out.println(currentPlayer.getName() + "'s Turn\n"
                + "Hand:\n" + currentPlayer.getHand().toString()+ RESET_TEXT
                + "Discard Pile: " + discardPile.displayLastCard()
                + "\nEnter card to play from your hand or DRAW to draw:");
        String move = console.readLine();
        return move;
    }

    private boolean takeTurn() throws IOException {
        boolean moveIsInvalid = true;
        while (moveIsInvalid) {
            String move = promptPlayerTurn();
            if (move.equalsIgnoreCase("draw")) {
                drawCard(currentPlayer);
                Card drawnCard = currentPlayer.getHand().displayLastCard();
                if (matchesDiscardCard(drawnCard)) {
                    promptToPlayDrawnCard(drawnCard);
                } else {
                    nextPlayer(1);
                }
                moveIsInvalid = false;
            } else {
                String[] cardAttr = move.split(" ");
                if (cardAttr.length == 2) {
                    cardAttr[0] = cardAttr[0].toLowerCase();
                    cardAttr[1] = cardAttr[1].toUpperCase();
                    Card card = getCardFromHand(cardAttr);
                    if (card != null && matchesDiscardCard(card)) {
                        if (playCard(card, cardAttr[0])) {
                            return true;
                        } 
                        moveIsInvalid = false;
                    } else {
                        if (card != null) {
                            currentPlayer.getHand().addCard(card);
                        }
                        System.out.println("I didn't understand that, try again");
                    }
                } else {
                    System.out.println("I didn't understand that, try again");
                }
            }
        }
        return false;
    }
    
    private boolean isWild(Card card) {
        return card.isWild();
    }
    
    private void promptToPlayDrawnCard(Card card) throws IOException {
        System.out.println("You drew a playable card: "+ card +
                "\nDo you want to play it? (y/n)");
        String answer = console.readLine();
        if (answer.equalsIgnoreCase("y")) {
            if (card.isWild()){
                while (true) {
                    System.out.println("What color would you like?");
                    answer = console.readLine();
                    String suitColor = UnoDeck.suitMap.get(answer.toLowerCase());
                    if (suitColor != null) {
                        card.getSuit().setText(suitColor);
                        break;
                    }
                }    
            }
            playCard(card, getCardSuitName(card));
        } else {
            nextPlayer(1);
        }
    }

    private void drawCard(Player player) {
        Card card = deck.removeLastCard();
        if (card != null) {
            player.getHand().addCard(card);
        } else {
            Card saveCard = discardPile.removeLastCard();
            discardPile.shuffleCards();
            deck.getCards().addAll(discardPile.getCards());
            discardPile.getCards().clear();
            discardPile.addCard(saveCard);
            player.getHand().addCard(deck.removeLastCard());
        }
    }

    private List<Card> cardList(Player player) {
        return player.getHand().getCards();
    }

    private Card getCardFromHand(String[] cardAttr) {
        for (Card card : cardList(currentPlayer)) {
            if (playerHasCard(cardAttr, card)) {
                currentPlayer.getHand().getCards().remove(card);
                return card;
            }
        }
        return null;
    }

    private Boolean playerHasCard(String[] cardAttr, Card card) {
        return ((card.getRank().getText().equals("W") && cardAttr[1].equalsIgnoreCase("w"))
            || (card.getRank().getText().equals("W4") && cardAttr[1].equalsIgnoreCase("w4"))
            || (getCardSuitName(card).equalsIgnoreCase(cardAttr[0])
            && card.getRank().getText().equalsIgnoreCase(cardAttr[1])));
    }
    
    private String getCardSuitName(Card card) {
        for (String suitName:UnoDeck.suitMap.keySet()) {
            String suitValue = UnoDeck.suitMap.get(suitName);
            if (suitValue.equals(card.getSuit().getText())) {
                return suitName;
            }
        }
        return null;
    }
    
    private boolean matchesDiscardCard(Card card) {
        Card topCard = discardPile.displayLastCard();
        return card.getRank().getText().equalsIgnoreCase(topCard.getRank().getText())
            || card.getSuit().getText().equalsIgnoreCase(topCard.getSuit().getText())
            || card.isWild();
    }
    
    private boolean playCard(Card card, String suitName) {
        card.getSuit().setText(UnoDeck.suitMap.get(suitName));
        discardPile.addCard(card);
        if (currentPlayer.getHand().getCards().isEmpty()){
            //player wins
            System.out.println(currentPlayer.getName()+" wins!");
            return true;
        } else {
            switch(card.getRank().getText()) {
                case "R":
                    directionOfPlay *= -1;
                    nextPlayer(1);
                    break;
                case "S":
                    nextPlayer(2);
                    break;
                case "D2":
                    nextPlayer(1);
                    Player nextPlayer = players.get(currentPlayerIndex);
                    for (int count=0; count<2; count++) {
                        drawCard(nextPlayer);
                    }
                    nextPlayer(1);
                    break;
                case "W4":
                    nextPlayer(1);
                    nextPlayer = players.get(currentPlayerIndex);
                    for (int count=0; count<4; count++) {
                        drawCard(nextPlayer);
                    }
                    nextPlayer(1);
                    break;
                default:
                    nextPlayer(1);
            }
            return false;
        }
    }
    
    private void nextPlayer(int offset) {
        currentPlayerIndex += offset * directionOfPlay;
        if (currentPlayerIndex == -2 ) {
            currentPlayerIndex = players.size()-1;
        }
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex -= players.size();
        } else if (currentPlayerIndex < 0) {
            currentPlayerIndex += players.size();
        }
    }
}
