
import java.util.Collections;
import java.util.Stack;

public class Deck {
    private Stack<Card> cards;

    public Deck() {
        cards = new Stack<>();
        initializeDeck();
    }

    // Método para inicializar o baralho com 52 cartas
    private void initializeDeck() {
        String[] suits = {"Paus", "Ouros", "Copas", "Espadas"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Valete", "Rainha", "Rei", "Ás"};
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.push(new Card(rank, suit));
            }
        }
        shuffleDeck();
    }

    // Método para embaralhar o baralho
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    // Método para retirar a carta do topo do baralho
    public Card drawCard() {
        return cards.pop();
    }

    // Método para verificar se o baralho está vazio
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // Método para obter o número de cartas restantes no baralho
    public int size() {
        return cards.size();
    }
}
