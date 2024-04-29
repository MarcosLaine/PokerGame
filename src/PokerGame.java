import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PokerGame {
    private List<Player> players;
    private Deck deck;
    private List<Card> communityCards;
    private int dealerIndex;
    private int currentPlayerIndex;
    private PokerGameGUI gameGUI;
    // private int currentBet;
    private int pot;
    private int currentPhase;
    private Player winner;

    public void restartGame() {
        deck = new Deck(); // Recria e embaralha o baralho
        deck.shuffleDeck();
        communityCards.clear(); // Limpa as cartas comunitárias
        for (Player player : players) {
            player.clearHand(); // Limpa a mão de cada jogador
            player.resetChips(1000); // Redefine as fichas dos jogadores, se necessário
        }
        dealerIndex = 0; // Pode modificar para escolher um novo dealer
        currentPlayerIndex = getNextPlayerIndex(dealerIndex);
        currentPhase = 0;
        pot = 0; // Limpa o pote
        gameGUI.updateUI(); // Atualiza a interface do usuário
    }

    public PokerGame(List<String> playerNames, int initialChips) {
        players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name, initialChips));
        }
        deck = new Deck();
        communityCards = new ArrayList<>();
        dealerIndex = 0;
        currentPlayerIndex = getNextPlayerIndex(dealerIndex);
        currentPhase = 0;
        deck.shuffleDeck();
    }

    public void startGame() {
        // Distribuição inicial de cartas
        dealNextCards();
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        while (players.get(currentPlayerIndex).hasFolded()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    public void dealNextCards() {
        switch (currentPhase) {
            case 0: // Pré-flop
                for (Player player : players) {
                    player.receiveCard(deck.drawCard());
                    player.receiveCard(deck.drawCard());
                }
                currentPhase = 1;
                break;
            case 1: // Flop
                communityCards.add(deck.drawCard());
                communityCards.add(deck.drawCard());
                communityCards.add(deck.drawCard());
                currentPhase = 2;
                break;
            case 2: // Turn
                communityCards.add(deck.drawCard());
                currentPhase = 3;
                break;
            case 3: // River
                communityCards.add(deck.drawCard());
                currentPhase = 4; // Próximo estado seria a avaliação do vencedor
                break;
            case 4: // Showdown
                determineWinner();
                break;
        }
        nextPlayer();
        gameGUI.updateUI();
    }

    private void determineWinner() {
        HandEvaluator evaluator = new HandEvaluator();
        winner = evaluator.evaluateWinner(players, communityCards);
        winner.addChips(pot);
        pot = 0;
        gameGUI.showWinner(winner);
    }

    private int getNextPlayerIndex(int currentIndex) {
        return (currentIndex + 1) % players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getPot() {
        return pot;
    }

    public void addToPot(int amount) {
        pot += amount;
    }

    public void setGameGUI(PokerGameGUI gui) {
        this.gameGUI = gui;
    }

    public String getCommunityCardString() {
        return communityCards.stream()
                .map(Card::toString)
                .collect(Collectors.joining("\n"));
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

}
