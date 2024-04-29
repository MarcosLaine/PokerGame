
public class Card implements Comparable<Card> {
    private String suit; // Naipe da carta: Paus, Ouros, Copas, Espadas
    private String rank; // Valor da carta: 2 a 10, Valete, Rainha, Rei, Ás

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    // Método para converter a carta em uma string legível
    @Override
    public String toString() {
        return rank + " de " + suit;
    }

    // Método para comparar cartas baseado no valor, usado para ordenação
    @Override
    public int compareTo(Card other) {
        return this.getCardValue() - other.getCardValue();
    }

    // Método auxiliar para obter o valor numérico de uma carta para comparação e ordenação
    int getCardValue() {
        switch (rank) {
            case "Ás":
                return 14; // O Ás pode ser o mais alto
            case "Rei":
                return 13;
            case "Rainha":
                return 12;
            case "Valete":
                return 11;
            default:
                return Integer.parseInt(rank); // Para cartas numéricas de 2 a 10
        }
    }
}
