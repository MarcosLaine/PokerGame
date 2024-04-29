import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private String name;
    private List<Card> hand;
    private int chips;
    private boolean hasFolded;

    public Player(String name, int chips) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.chips = chips;
        this.hasFolded = false;
    }

    // Métodos de acesso e modificação
    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        this.chips += amount;
    }

    public void bet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Player não tem fichas suficientes para a aposta");
        }
        this.chips -= amount;
    }

    public void fold() {
        this.hasFolded = true;
    }

    public boolean hasFolded() {
        return this.hasFolded;
    }

    public void clearHand() {
        this.hand.clear();
    }

    // Adiciona cartas à mão do jogador
    public void receiveCard(Card card) {
        this.hand.add(card);
    }

    public void resetChips(int initialChips) {
        this.chips = initialChips;
    }

    public String getHandString() {
    return hand.stream()
               .map(Card::toString)
               .collect(Collectors.joining("\n"));
}

}
