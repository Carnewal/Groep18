/*
 * Title: 	21 Blackjack - Beating the Odds
 * File: 	Blackjack.java
 *
 * Description:
 *
 * This program simulates the game of Blackjack and the
 * best known way to play Blackjack. The simulated player
 * uses basic game play strategy to eliminate as much of
 * the house edge as possible. To gain an edge over the
 * house the player utilizes the "counting cards" method
 * to decide how to bet and play each hand.
 *
 * CPSC 481 - Artificial Intelligence
 *
 * Group Project - Group #5		
 * 
 * Bryan Perez
 * Jeffrey White
 *
 * Date:	November 30, 2010


 Aangepast en gebruikt voor project onderzoekstechnieken.
 Bert Roman - Groep 18
 */

import java.util.LinkedList;
import java.util.Random;

public class Blackjack {

    private int totalDeckSize;
    private int gameCount;
    private String card[];					// set of all cards in a deck
    private LinkedList<Integer> cardDeck;	// actual order of cards left in deck
    private LinkedList<String> dlist;		// cards dealt to dealer
    private LinkedList<String> plist;		// cards dealt to player
    private LinkedList<String> plist2;		// used when player splits
    private int playerCardSum;				// total sum of player's cards
    private int playerCardSum2;      		// used when player splits
    private int dealerCardSum;				// total sum of dealer's cards
    private int winCount;					// # of wins in session
    private int loseCount;					// # of loses in session
    private int runningCount;				// the "count" of cards played
    private int trueCount;					// runningCount/decksLeft
    private int currentBet;					// player's bet
    private int totalChips;					// starting chips
    private int minBet; 					// minimum bet player can make
    private boolean insurance;				// true or false if player wants insurance
    private int insuranceBet;				// ammount player bets on insurance
    private int totalWinnings;				// total chips won in session
    private int totalLoss;					// total chips lost in session
    private boolean blackjack; 				// if player hit blackjack
    private boolean split;					// if player split cards
    private boolean soft17;					// if dealer has a soft 17
    private int decksLeft;					// total decks left in game play

    /////////////////////////////////////////////////////////////////////////////////////
    // Rule based systems for general gameplay
    // Main Blackjack Strategy
    private String[][] mainStrategyTable = new String[][]{
        //                                Dealers Card Up
        /*Your Hand        2    3    4    5    6    7    8    9   10    A   */
        /*<=8*/ /*0*/{"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*9*/ /*1*/ {"H", "D", "D", "D", "D", "H", "H", "H", "H", "H"},
        /*10*/ /*2*/ {"D", "D", "D", "D", "D", "D", "D", "D", "H", "H"},
        /*11*/ /*3*/ {"D", "D", "D", "D", "D", "D", "D", "D", "D", "H"},
        /*12*/ /*4*/ {"H", "H", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*13*/ /*5*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*14*/ /*6*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*15*/ /*7*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*16*/ /*8*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*17+*/ /*9*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*A2*/ /*10*/ {"H", "H", "H", "D", "D", "H", "H", "H", "H", "H"},
        /*A3*/ /*11*/ {"H", "H", "H", "D", "D", "H", "H", "H", "H", "H"},
        /*A4*/ /*12*/ {"H", "H", "D", "D", "D", "H", "H", "H", "H", "H"},
        /*A5*/ /*13*/ {"H", "H", "D", "D", "D", "H", "H", "H", "H", "H"},
        /*A6*/ /*14*/ {"H", "D", "D", "D", "D", "H", "H", "H", "H", "H"},
        /*A7*/ /*15*/ {"S", "D", "D", "D", "D", "S", "S", "H", "H", "H"},
        /*A8*/ /*16*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*A9*/ /*17*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*2-2*/ /*18*/ {"P", "P", "P", "P", "P", "P", "H", "H", "H", "H"},
        /*3-3*/ /*19*/ {"P", "P", "P", "P", "P", "P", "H", "H", "H", "H"},
        /*4-4*/ /*20*/ {"H", "H", "H", "P", "P", "H", "H", "H", "H", "H"},
        /*5-5*/ /*21*/ {"D", "D", "D", "D", "D", "D", "D", "D", "H", "H"},
        /*6-6*/ /*22*/ {"P", "P", "P", "P", "P", "H", "H", "H", "H", "H"},
        /*7-7*/ /*23*/ {"P", "P", "P", "P", "P", "P", "H", "H", "H", "H"},
        /*8-8*/ /*24*/ {"P", "P", "P", "P", "P", "P", "P", "P", "P", "P"},
        /*9-9*/ /*25*/ {"P", "P", "P", "P", "P", "S", "P", "P", "S", "S"},
        /*10-10*/ /*26*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*A-A*/ /*27*/ {"P", "P", "P", "P", "P", "P", "P", "P", "P", "P"}};
    /*
     * H - Hit     S - Stand     D - Double Down     P - Split
     */

    // Basic strategy    
    private String[][] basicStrategyTable = new String[][]{
        //                                Dealers Card Up
        /*Your Hand        2    3    4    5    6    7    8    9   10    A   */
        /*<=8*/ /*0*/{"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*9*/ /*1*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*10*/ /*2*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*11*/ /*3*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*12*/ /*4*/ {"H", "H", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*13*/ /*5*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*14*/ /*6*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*15*/ /*7*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*16*/ /*8*/ {"S", "S", "S", "S", "S", "H", "H", "H", "H", "H"},
        /*17+*/ /*9*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*A2*/ /*10*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*A3*/ /*11*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*A4*/ /*12*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*A5*/ /*13*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*A6*/ /*14*/ {"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"},
        /*A7*/ /*15*/ {"S", "H", "H", "H", "H", "S", "S", "H", "H", "H"},
        /*A8*/ /*16*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"},
        /*A9*/ /*17*/ {"S", "S", "S", "S", "S", "S", "S", "S", "S", "S"}};
    /*
     * H - Hit     S - Stand
     */

    // End of rule based systems for general gameplay
    /////////////////////////////////////////////////////////////////////////////////////
    public Blackjack() {
        // Initialize variables

        createDeckOfCards(6);		// AI is tuned for 6 decks - recommend not changing this
        gameCount = 1;
        dlist = new LinkedList<String>();
        plist = new LinkedList<String>();
        plist2 = new LinkedList<String>();
        playerCardSum = 0;
        playerCardSum2 = 0;
        dealerCardSum = 0;
        winCount = 0;
        loseCount = 0;
        currentBet = 0;
        totalChips = 1000;		// this can be adjusted
        minBet = 10;				// this can be adjusted
        runningCount = 0;
        trueCount = 0;
        insurance = false;
        insuranceBet = 0;
        totalWinnings = 0;
        totalLoss = 0;
        soft17 = false;
        decksLeft = 0;
    }

    /*
     *  Precondition: a new instance of Blackjack has been created.
     *  Postcondition: the player will play Blackjack for the number
     *  of desired games. End statistics of gameplay will be printed
     *  at the end.
     */
    public void play() {
        shuffleDeck();
        int numberOfGames = 45;		// this can be adjusted
        for (int x = 0; x < numberOfGames; x++) {
            playGame();
        }

        // Output session summary
        System.out.printf("------------------- Play Summary --------------------\n");
        System.out.printf("\nPlayer Wins:      %d\n", winCount);
        System.out.printf("Player Loses:     %d\n", loseCount);
        System.out.printf("Win Percent:      %.2f\n", ((float) winCount / ((float) winCount + (float) loseCount)) * 100.00);
        System.out.printf("Total Winnings:   %d\n", totalWinnings);
        System.out.printf("Total Loss:       %d\n", totalLoss);
        System.out.printf("Winnings Percent: %.2f\n\n", ((float) totalWinnings / ((float) totalWinnings + (float) totalLoss)) * 100.00);

        if (totalWinnings > totalLoss) {
            System.out.printf("Player is up: %d chips\n\n", totalWinnings - totalLoss);
        } else if (totalWinnings < totalLoss) {
            System.out.printf("Player is down: %d chips\n\n", totalLoss - totalWinnings);
        } else {
            System.out.printf("Player broke even.\n\n");
        }
    }

    /*
     *  Precondition: card decks have been created and shuffled.
     *  Postcondition: the end result of the game will be printed.
     */
    public void playGame() {
        int initCardDeckSize = cardDeck.size();
        // initialize game values
        blackjack = false;
        split = false;
        insurance = false;
        int preGameChipCount = totalChips;

        //System.out.printf("--------------------- Game %S ---------------------\n", gameCount++);
        System.out.printf("%d\t", trueCount);

        //System.out.printf("Running count:\t%d\tTrue count:\t%d\n", runningCount, trueCount);
        //System.out.printf("Cards left:\t%d\tDecks left:\t%d\n", cardDeck.size(), decksLeft);
        placeBet();		// initial bet before game starts

        if (cardDeck.size() < 4) {
            shuffleDeck();	// not enough cards to start game
        }
        // deal first set of cards
        String dealersCard1 = dealCard();
        String playersCard1 = dealCard();
        String dealersCard2 = dealCard();
        String playersCard2 = dealCard();

        dlist.add(dealersCard1.substring(0, 1));
        dlist.add(dealersCard2.substring(0, 1));
        updateDealerHand();

        System.out.printf("%S\t%S\t%S\t", dealersCard1.substring(0, 1), playersCard1.substring(0, 1), playersCard2.substring(0, 1));

        /////////////////////////////////////////////////////////////////////////////////////
        // Player's Logic
        // decide on insurance
        if (trueCount >= 3 && dealersCard1.substring(0, 1).equals("A") && currentBet / 2 <= totalChips) {
            insurance = true;
            insuranceBet = currentBet / 2;
            totalChips -= insuranceBet;
            //System.out.printf("Insurance bet: $%d\n", insuranceBet);
        }

        // check to see if insurance paid off
        if (insurance) {
            if (dealersCard2.substring(0, 1).equals("1") || dealersCard2.substring(0, 1).equals("J") || dealersCard2.substring(0, 1).equals("Q") || dealersCard2.substring(0, 1).equals("K")) {
                totalChips += insuranceBet * 2;
                //System.out.printf("Dealer has 21, player wins %S off insurance bet.\n", insuranceBet * 2);
                totalWinnings += insuranceBet;
            } else {
                // System.out.println("Insurance did not pay off.");
                totalLoss += insuranceBet;
            }
        }

        plist.add(playersCard1.substring(0, 1));
        plist.add(playersCard2.substring(0, 1));
        updatePlayerHand();
        //System.out.printf("Player's card sum:\t%S\n", playerCardSum);

        // check for Blackjack
        if (hasBlackjack(playersCard1.substring(0, 1), playersCard2.substring(0, 1))) {
            blackjack = true;
        } else {	// does not have Blackjack; play with basic strategy
            String playerAction;

            playerAction = getPlayerAction(dealersCard1.substring(0, 1), playersCard1.substring(0, 1), playersCard2.substring(0, 1));
			//System.out.printf("Player's Action:\t%S\n", playerAction);

            // execute player's action
            if (playerAction.equals("H")) {
                hit(dealersCard1.substring(0, 1));	// player chooses to hit
            } else if (playerAction.equals("S")) {
                //System.out.printf("-> Player stands\n");	// do nothing player chooses to stand
            } else if (playerAction.equals("D")) {
                doubleDown(dealersCard1.substring(0, 1));	// player chooses to double down
            } else if (playerAction.equals("P")) {
                split(playersCard1, playersCard2, dealersCard1.substring(0, 1));	// player chooses to split
            } else
				;	// move undefined; error
        }

        /////////////////////////////////////////////////////////////////////////////////////
        // Dealers logic
        String cardn;
        // System.out.printf("\nDealer's 2nd card:\t%S\n", dealersCard2);
        //updateCardCount(dealersCard2.substring( 0, 1 ), " ", " ");
        //System.out.printf("Dealer's card sum:\t%S\n", dealerCardSum);

        while (dealerCardSum < 17 || soft17) {
            if (soft17) {
                //System.out.printf("**Dealer has a soft 17**\n");
            }
            //System.out.printf("-> Dealer hits\n");
            cardn = dealCard();
            dlist.add(cardn.substring(0, 1));
            updateDealerHand();
           // System.out.printf("Dealer gets card:\t%S\n", cardn);
            // System.out.printf("Dealer's new card Sum:\t%S\n", dealerCardSum);
        }

		/////////////////////////////////////////////////////////////////////////////////////
        // Output game status
        //System.out.println("");
        if (blackjack) {
            System.out.printf("1\t");
        }
        if (!blackjack) {
            System.out.printf("0\t");
        }

        // Check for winner
        if (split) {
            // check hand 1
            if (playerCardSum > 21) {
                //System.out.printf("\nPlayer busts on hand 1 and dealer wins\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum > 21) {
                //System.out.printf("\nDealer busts and player wins on hand 1\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                // player wins 1:1
                totalChips += currentBet;
                totalWinnings += currentBet;
            } else if (dealerCardSum > playerCardSum) {
                //System.out.printf("\nDealer wins player loses on hand 1\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum == playerCardSum) {
                // player gets bet back
                totalChips += currentBet;
                //System.out.printf("\nPush on hand 1\n\n");
            } else {
                //System.out.printf("\nPlayer wins on hand 1\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                // player wins 1:1
                totalChips += currentBet;
                totalWinnings += currentBet;
            }

            // check hand 2
            if (playerCardSum2 > 21) {
                //System.out.printf("\nPlayer busts on hand 2 and dealer wins\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum > 21) {
                //System.out.printf("\nDealer busts and player wins on hand 2\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                // player wins 1:1
                totalChips += currentBet;
                totalWinnings += currentBet;
            } else if (dealerCardSum > playerCardSum2) {
                //System.out.printf("\nDealer wins and player loses on hand 2\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum == playerCardSum2) {
                //System.out.printf("\nPush on hand 2\n\n");
                // player gets bet back
                totalChips += currentBet;
            } else {
                //System.out.printf("\nPlayer wins on hand 2\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                // player wins 1:1
                totalChips += currentBet;
                totalWinnings += currentBet;
            }
        } else {
            if (playerCardSum > 21) {
                //System.out.printf("\nPlayer busts and dealer wins\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum > 21) {
                //System.out.printf("\nDealer busts and player wins\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                if (blackjack) {
                    // player wins 3:2
                    totalChips += currentBet + currentBet / 2;
                    totalWinnings += currentBet + currentBet / 2;
                } else {
                    // player wins 1:1
                    totalChips += currentBet;
                    totalWinnings += currentBet;
                }
            } else if (dealerCardSum > playerCardSum) {
                //System.out.printf("\nDealer wins\n\n");
                loseCount++;
                totalLoss += currentBet;
            } else if (dealerCardSum == playerCardSum) {
                //System.out.printf("\nPush\n\n");
                // player gets bet back
                totalChips += currentBet;
            } else {
                //System.out.printf("\nPlayer wins\n\n");
                winCount++;
                // player gets bet back
                totalChips += currentBet;
                if (blackjack) {
                    // player wins 3:2
                    totalChips += currentBet + currentBet / 2;
                    totalWinnings += currentBet + currentBet / 2;
                } else {
                    // player wins 1:1
                    totalChips += currentBet;
                    totalWinnings += currentBet;
                }
            }
        }

        if (totalChips - preGameChipCount < 0) {
            System.out.print("0\t");
        } else if (totalChips - preGameChipCount == 0) {
            System.out.print("2\t");
        } else {
            System.out.print("1\t");
        }

        System.out.printf("%d\t%d\n", initCardDeckSize - cardDeck.size(), cardDeck.size());
        //System.out.printf("Player's chip difference:\t%d\n", totalChips - preGameChipCount);
        //System.out.printf("Player's chip count:\t\t%d\n\n", totalChips);
        plist.clear();
        plist2.clear();
        dlist.clear();
    }


    /*
     *  Precondition: substring of the card updating the count is passed.
     *  Postcondition: runningCount, trueCount, and decksLeft have been updated.
     */
    public void updateCardCount(String card) {
        if (card.equals("2") || card.equals("3") || card.equals("7")) {
            runningCount += 1;
        } else if (card.equals("9")) {
            runningCount -= 1;
        } else if (card.equals("4") || card.equals("5") || card.equals("6")) {
            runningCount += 2;
        } else if (card.equals("1") || card.equals("J") || card.equals("Q") || card.equals("K")) // "1" = 10
        {
            runningCount -= 2;
        } else
			; // do nothing

        // numbers of decks left
        decksLeft = (cardDeck.size() / 52) + 1;

        // update true count
        trueCount = runningCount / decksLeft;
    }

    /*	
     *  Precondition: minBet and trueCount exists.
     *  Postcondition: a bet based on trueCount is placed and total
     *  chips is updated.
     */
    public void placeBet() {
        int minimum = minBet;

         //Rule based system to decide bet ammount
		if(trueCount < 0)
		{
        if (totalChips < minimum) {
            currentBet = totalChips;
            totalChips = 0;
        } else {
            currentBet = minimum;
        }
		}

		switch(trueCount)
		{
			case 0:
				if (totalChips < minimum)
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = minimum;
			break;
			case 1:
				if (totalChips < minimum*2)
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = minimum*2;
			break;
			case 2:
				if (totalChips < minimum*4)
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = minimum*4;
			break;
			case 3:
				if (totalChips < (minimum*8))
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = (minimum*8);
			break;
			case 4:
				if (totalChips < (minimum*10))
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = (minimum*10);
			break;
			case 5:
				if (totalChips < (minimum*12))
				{
					currentBet = totalChips;
					totalChips=0;
				}
				else
					currentBet = (minimum*12);
			break;
		}

		if (trueCount >= 6)
		{
			if (totalChips < (minimum*12))
			{
				currentBet = totalChips;
				totalChips=0;
			}
			else
				currentBet = (minimum*12);
		}
        totalChips -= currentBet;	// deduct bet from total chips
        //System.out.printf("Player bets:\t%S\n", currentBet);
    }


    /*
     *  Precondition: dealer's visible card is passed.
     *  Postcondition: will either call itself to hit or
     *  player stands.
     */
    public void hit(String dCard) {
        //System.out.printf("-> Player hits\n");
        // deal next card to player
        String newCard = dealCard();
        //System.out.printf("Player receives:\t%S\n", newCard);
        plist.add(newCard.substring(0, 1));
        updatePlayerHand();
        //System.out.printf("Player's new card sum:\t%S\n", playerCardSum);

        String nextAction = getNextPlayerAction(dCard);

        if (nextAction.equals("H")) {
            hit(dCard);	// player hits
        } else if (nextAction.equals("S")) {
            //System.out.printf("-> Player stands\n");
        }
    }


    /*
     *  Precondition: in the middle of a split and dealer's visible card is passed.
     *  Postcondition: will either call itself to hit or
     *  player stands.
     */
    public void hit2(String dCard) {
        //System.out.printf("-> Player hits\n");
        // deal next card to player
        String newCard = dealCard();
        //System.out.printf("Player receives:\t%S\n", newCard);
        plist2.add(newCard.substring(0, 1));
        updatePlayerHand2();
        //System.out.printf("Player's new card sum:\t%S\n", playerCardSum2);

        String nextAction = getNextPlayerAction2(dCard);

        if (nextAction.equals("H")) {
            hit2(dCard);	// player hits
        } else if (nextAction.equals("S")) {
            // System.out.printf("-> Player stands on hand 2\n");
        }
    }


    /*
     *  Precondition: dealer's visible card is passed.
     *  Postcondition: will either call hit or
     *  player stands.
     */
    public void doubleDown(String dCard) {
        //System.out.printf("-> Player doubles down\n");
        // double player's bet
        totalChips -= currentBet;
        currentBet = currentBet * 2;

        // deal next card to player
        String newCard = dealCard();
        //System.out.printf("Player receives:\t%S\n", newCard);
        plist.add(newCard.substring(0, 1));
        updatePlayerHand();
        //System.out.printf("Player's new card sum:\t%S\n", playerCardSum);

        String nextAction = getNextPlayerAction(dCard);

        if (nextAction.equals("H")) {
            hit(dCard);	// player hits
        } else if (nextAction.equals("S")) {
            //System.out.printf("-> Player stands\n");
        }
    }


    /*
     *  Precondition: both player's cards and dealer's visible card is passed.
     *  Postcondition: will either call itself to hit on each hand or
     *  player stands on each hand.
     */
    public void split(String pCard1, String pCard2, String dCard) {
        split = true;
        //System.out.printf("-> Player splits\n");
        plist.clear(); // empty first hand
        // create second bet for second hand
        totalChips -= currentBet;
        // split into two hands
        plist.add(pCard1.substring(0, 1));
        plist2.add(pCard2.substring(0, 1));

        // deal new cards to each hand
        String newCard1 = dealCard();
        String newCard2 = dealCard();
        plist.add(newCard1.substring(0, 1));
        plist2.add(newCard2.substring(0, 1));
        updatePlayerHand();
        updatePlayerHand2();

       // System.out.printf("Hand 1:\t%S\t%S\tHand 2:\t%S\t%S\n", pCard1, newCard1, pCard2, newCard2);
        //System.out.printf("Hand 1 Sum:\t%S\tHand 2 sum:\t%S\n", playerCardSum, playerCardSum2);
        // play hand 1
        //System.out.printf("\n*Hand 1 Actions*\n");
        String nextAction = getNextPlayerAction(dCard);

        if (nextAction.equals("H")) {
            hit(dCard);	// player hits
        } else if (nextAction.equals("S")) {
            //System.out.printf("-> Player stands on hand 1\n");
        }

        // play hand 2
        //System.out.printf("\n*Hand 2 Actions*\n");
        nextAction = getNextPlayerAction2(dCard);

        if (nextAction.equals("H")) {
            hit2(dCard);	// player hits
        } else if (nextAction.equals("S")) {
            //System.out.printf("-> Player stands on hand 2\n");
        }
    }

    /*
     *  Precondition: dealer's visible card is passed.
     *  Postcondition: player's action is returned.
     */
    public String getNextPlayerAction(String dCard) {
        int playerHandIndex = getPlayerHandIndex();
        int dealerHandIndex = getDealerCardIndex(dCard);

        return basicStrategyTable[playerHandIndex][dealerHandIndex];
    }

    /*
     *  Precondition: dealer's visible card is passed.
     *  Postcondition: player's action is returned.
     */
    public String getNextPlayerAction2(String dCard) {
        int playerHandIndex = getPlayerHandIndex2();
        int dealerHandIndex = getDealerCardIndex(dCard);

        return basicStrategyTable[playerHandIndex][dealerHandIndex];
    }

    public int getPlayerHandIndex() {
        // Sum up all cards except 1 ace if an ace exists
        if (plist.contains("A")) {
            int cardSum = 0;
            boolean foundAce = false;

            // add all cards except Aces
            for (int x = 0; x < plist.size(); x++) {
                if (!plist.get(x).equals("A")) {
                    if (plist.get(x).equals("1") || plist.get(x).equals("J") || plist.get(x).equals("Q") || plist.get(x).equals("K")) {
                        cardSum += 10;
                    } else {
                        cardSum += Integer.parseInt(plist.get(x));
                    }
                }
            }

            // add all but 1 ace
            for (int x = 0; x < plist.size(); x++) {
                if (plist.get(x).equals("A")) {
                    if (foundAce) {
                        if (cardSum > 10) {
                            cardSum += 1;
                        } else {
                            cardSum += 11;
                        }
                    } else {
                        foundAce = true;	// skip the first ace
                    }
                }
            }

            // return player hand index for basic strategy table
            if (cardSum <= 9) {
                return cardSum + 8;
            } else if (playerCardSum <= 8) {
                return 0;
            } else if (playerCardSum >= 17) {
                return 9;
            } else {
                return playerCardSum - 8;
            }
        } else {   // no aces in hand
            if (playerCardSum <= 8) {
                return 0;
            } else if (playerCardSum >= 17) {
                return 9;
            } else {
                return playerCardSum - 8;
            }
        }
    }

    public int getPlayerHandIndex2() {
        // Sum up all cards except 1 ace if an ace exists
        if (plist2.contains("A")) {
            int cardSum = 0;
            boolean foundAce = false;

            // add all cards except Aces
            for (int x = 0; x < plist2.size(); x++) {
                if (!plist2.get(x).equals("A")) {
                    if (plist2.get(x).equals("1") || plist2.get(x).equals("J") || plist2.get(x).equals("Q") || plist2.get(x).equals("K")) {
                        cardSum += 10;
                    } else {
                        cardSum += Integer.parseInt(plist2.get(x));
                    }
                }
            }

            // add all but 1 ace
            for (int x = 0; x < plist2.size(); x++) {
                if (plist2.get(x).equals("A")) {
                    if (foundAce) {
                        if (cardSum > 10) {
                            cardSum += 1;
                        } else {
                            cardSum += 11;
                        }
                    } else {
                        foundAce = true;	// skip the first ace
                    }
                }
            }

            // return player hand index for basic strategy table
            if (cardSum <= 9) {
                return cardSum + 8;
            } else if (playerCardSum2 <= 8) {
                return 0;
            } else if (playerCardSum2 >= 17) {
                return 9;
            } else {
                return playerCardSum2 - 8;
            }
        } else {   // no aces in hand
            if (playerCardSum2 <= 8) {
                return 0;
            } else if (playerCardSum2 >= 17) {
                return 9;
            } else {
                return playerCardSum2 - 8;
            }
        }
    }

    public void updatePlayerHand() {
        // reset sum
        playerCardSum = 0;

        if (plist.contains("A")) {
            // add all cards except Aces
            for (int x = 0; x < plist.size(); x++) {
                if (!plist.get(x).equals("A")) {
                    if (plist.get(x).equals("1") || plist.get(x).equals("J") || plist.get(x).equals("Q") || plist.get(x).equals("K")) {
                        playerCardSum += 10;
                    } else {
                        playerCardSum += Integer.parseInt(plist.get(x));
                    }
                }
            }

            // add aces
            for (int x = 0; x < plist.size(); x++) {
                if (plist.get(x).equals("A")) {
                    if (playerCardSum > 10) {
                        playerCardSum += 1;
                    } else {
                        playerCardSum += 11;
                    }
                }
            }
        } else {   // no aces in hand
            for (int x = 0; x < plist.size(); x++) {
                if (plist.get(x).equals("1") || plist.get(x).equals("J") || plist.get(x).equals("Q") || plist.get(x).equals("K")) {
                    playerCardSum += 10;
                } else {
                    playerCardSum += Integer.parseInt(plist.get(x));
                }
            }
        }
    }

    public void updatePlayerHand2() {
        // reset sum
        playerCardSum2 = 0;

        if (plist2.contains("A")) {
            // add all cards except Aces
            for (int x = 0; x < plist2.size(); x++) {
                if (!plist2.get(x).equals("A")) {
                    if (plist2.get(x).equals("1") || plist2.get(x).equals("J") || plist2.get(x).equals("Q") || plist2.get(x).equals("K")) {
                        playerCardSum2 += 10;
                    } else {
                        playerCardSum2 += Integer.parseInt(plist2.get(x));
                    }
                }
            }

            // add aces
            for (int x = 0; x < plist2.size(); x++) {
                if (plist2.get(x).equals("A")) {
                    if (playerCardSum2 > 10) {
                        playerCardSum2 += 1;
                    } else {
                        playerCardSum2 += 11;
                    }
                }
            }
        } else {   // no aces in hand
            for (int x = 0; x < plist2.size(); x++) {
                if (plist2.get(x).equals("1") || plist2.get(x).equals("J") || plist2.get(x).equals("Q") || plist2.get(x).equals("K")) {
                    playerCardSum2 += 10;
                } else {
                    playerCardSum2 += Integer.parseInt(plist2.get(x));
                }
            }
        }
    }


    /*
     *  Preconditions: two cards are passed.
     *  Postconditions: condition of blackjack is returned.
     */
    public boolean hasBlackjack(String card1, String card2) {
        if ((card1.equals("1") || card1.equals("J") || card1.equals("Q") || card1.equals("K")) && (card2.equals("A"))) {
            return true;	// has Blackjack
        } else if ((card2.equals("1") || card2.equals("J") || card2.equals("Q") || card2.equals("K")) && (card1.equals("A"))) {
            return true;	// has Blackjack
        } else {
            return false;	// no Blackjack
        }
    }

    /*
     *  Preconditions: player's first two cards and visible dealer card is 
     *  passed through parameter.
     *  Postconditions: best suited action is returned based on basic game
     *  play strategy table.
     */
    public String getPlayerAction(String dCard, String pCard1, String pCard2) {
        int playerHandIndex = getPlayerHandIndex(pCard1, pCard2);
        int dealerHandIndex = getDealerCardIndex(dCard);

        return mainStrategyTable[playerHandIndex][dealerHandIndex];
    }

    /*
     *  Precondition: both of player's first dealt cards are passed.
     *  Postcondition: the player's hand index for strategy table is returned.
     */
    public int getPlayerHandIndex(String pCard1, String pCard2) {
        if (pCard1.equals(pCard2)) // check for pairs
        {
            if (pCard1.equals("1") || pCard1.equals("J") || pCard1.equals("Q") || pCard1.equals("K")) {
                return 26;		// pair of 10's
            } else if (pCard1.equals("A")) {
                return 27;		// pair of Aces
            } else {
                return (Integer.parseInt(pCard1) + 16);
            }
        } else if ((pCard1.equals("1") || pCard1.equals("J") || pCard1.equals("Q") || pCard1.equals("K"))
                && (pCard2.equals("1") || pCard2.equals("J") || pCard2.equals("Q") || pCard2.equals("K"))) {
            return 26;				// both card values = 10
        } else if (pCard1.equals("A")) {
            return (Integer.parseInt(pCard2) + 8);	// A2-A9
        } else if (pCard2.equals("A")) {
            return (Integer.parseInt(pCard1) + 8); // A2-A9
        } else {   // no aces, pairs, or two value 10's in hand 
            int sum = 0;

            // get two card sum
            if (pCard1.equals("1") || pCard1.equals("J") || pCard1.equals("Q") || pCard1.equals("K")) {
                sum = 10 + Integer.parseInt(pCard2);	// first card is a 10
            } else if (pCard2.equals("1") || pCard2.equals("J") || pCard2.equals("Q") || pCard2.equals("K")) {
                sum = 10 + Integer.parseInt(pCard1);	// second card is a 10
            } else {
                sum = Integer.parseInt(pCard1) + Integer.parseInt(pCard2); // no 10's
            }
            // return correct index based on card sum
            if (sum <= 8) {
                return 0;
            } else if (sum >= 17) {
                return 9;
            } else {
                return (sum - 8);
            }
        }
    }

    /*
     *  Precondition: dealer's visible card passed through parameter.
     *  Postcondition: the dealer's hand index for strategy table is returned.
     */
    public int getDealerCardIndex(String dCard) {
        if (dCard.equals("1") || dCard.equals("J") || dCard.equals("Q") || dCard.equals("K")) {
            return 8;									// Dealer's card = 10, J, Q, K
        } else if (dCard.equals("A")) {
            return 9;									// Dealer's card = A
        } else {
            return (Integer.parseInt(dCard) - 2);	// Dealer's card = 2-9
        }
    }

    // Create deck of cards
    // D=diamonds, H=hearts, S=spades, C=clubs
    public void createDeckOfCards(int numberOfDecks) {
        totalDeckSize = 52 * numberOfDecks;
        card = new String[totalDeckSize];
        cardDeck = new LinkedList<Integer>();

        for (int x = 0; x < numberOfDecks; x++) {
            card[0 + (x * 52)] = "AD";
            card[1 + (x * 52)] = "2D";
            card[2 + (x * 52)] = "3D";
            card[3 + (x * 52)] = "4D";
            card[4 + (x * 52)] = "5D";
            card[5 + (x * 52)] = "6D";
            card[6 + (x * 52)] = "7D";
            card[7 + (x * 52)] = "8D";
            card[8 + (x * 52)] = "9D";
            card[9 + (x * 52)] = "10D";
            card[10 + (x * 52)] = "JD";
            card[11 + (x * 52)] = "QD";
            card[12 + (x * 52)] = "KD";
            card[13 + (x * 52)] = "AH";
            card[14 + (x * 52)] = "2H";
            card[15 + (x * 52)] = "3H";
            card[16 + (x * 52)] = "4H";
            card[17 + (x * 52)] = "5H";
            card[18 + (x * 52)] = "6H";
            card[19 + (x * 52)] = "7H";
            card[20 + (x * 52)] = "8H";
            card[21 + (x * 52)] = "9H";
            card[22 + (x * 52)] = "10H";
            card[23 + (x * 52)] = "JH";
            card[24 + (x * 52)] = "QH";
            card[25 + (x * 52)] = "KH";
            card[26 + (x * 52)] = "AS";
            card[27 + (x * 52)] = "2S";
            card[28 + (x * 52)] = "3S";
            card[29 + (x * 52)] = "4S";
            card[30 + (x * 52)] = "5S";
            card[31 + (x * 52)] = "6S";
            card[32 + (x * 52)] = "7S";
            card[33 + (x * 52)] = "8S";
            card[34 + (x * 52)] = "9S";
            card[35 + (x * 52)] = "10S";
            card[36 + (x * 52)] = "JS";
            card[37 + (x * 52)] = "QS";
            card[38 + (x * 52)] = "KS";
            card[39 + (x * 52)] = "AC";
            card[40 + (x * 52)] = "2C";
            card[41 + (x * 52)] = "3C";
            card[42 + (x * 52)] = "4C";
            card[43 + (x * 52)] = "5C";
            card[44 + (x * 52)] = "6C";
            card[45 + (x * 52)] = "7C";
            card[46 + (x * 52)] = "8C";
            card[47 + (x * 52)] = "9C";
            card[48 + (x * 52)] = "10C";
            card[49 + (x * 52)] = "JC";
            card[50 + (x * 52)] = "QC";
            card[51 + (x * 52)] = "KC";
        }
    }

    // Precondition: cardDeck is empty
    // Postcondtion: cardDeck has the indices for all cards in a deck
    public void shuffleDeck() {
        //System.out.printf("\nShuffling...\n");
        int cardIndex;
        boolean foundUniqueIndex;
        Random randomNumber = new Random();
        while (cardDeck.size() < totalDeckSize) {
            foundUniqueIndex = false;

            while (!foundUniqueIndex) {
                cardIndex = randomNumber.nextInt(totalDeckSize);
                if (!cardDeck.contains(cardIndex)) {
                    //System.out.printf( "%S\n",cardIndex );
                    cardDeck.push(cardIndex);
                    foundUniqueIndex = true;
                }
            }
        }
        //System.out.printf("\nCards Shuffled.\n\n");
        runningCount = 0;
        trueCount = 0;
    }

    public String dealCard() {
        if (cardDeck.isEmpty()) {
            shuffleDeck();
        }
        String cardDealt = card[cardDeck.remove()];
        updateCardCount(cardDealt.substring(0, 1));

        return cardDealt;
    }

    public void updateDealerHand() {
        // reset sum
        dealerCardSum = 0;
        soft17 = false;
        boolean ace11 = false;

        if (dlist.contains("A")) {
            // add all cards except Aces
            for (int x = 0; x < dlist.size(); x++) {
                if (!dlist.get(x).equals("A")) {
                    if (dlist.get(x).equals("1") || dlist.get(x).equals("J") || dlist.get(x).equals("Q") || dlist.get(x).equals("K")) {
                        dealerCardSum += 10;
                    } else {
                        dealerCardSum += Integer.parseInt(dlist.get(x));
                    }
                }
            }

            // add aces
            for (int x = 0; x < dlist.size(); x++) {
                if (dlist.get(x).equals("A")) {
                    if (dealerCardSum > 10) {
                        dealerCardSum += 1;
                    } else {
                        dealerCardSum += 11;
                        ace11 = true;
                    }
                }
            }
            // check for soft 17
            if (ace11 && dealerCardSum == 17) {
                soft17 = true;
            }
        } else {   // no aces in hand
            for (int x = 0; x < dlist.size(); x++) {
                if (dlist.get(x).equals("1") || dlist.get(x).equals("J") || dlist.get(x).equals("Q") || dlist.get(x).equals("K")) {
                    dealerCardSum += 10;
                } else {
                    dealerCardSum += Integer.parseInt(dlist.get(x));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Blackjack game = new Blackjack();
            game.play();
        } catch (Exception e) {
            System.out.printf("\n\nException: %S\n\n", e);
        }
    }
}
