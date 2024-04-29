import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandEvaluator {

    // Definição de constantes para os rankings das mãos de pôquer
    private static final int HIGH_CARD = 1;
    private static final int ONE_PAIR = 2;
    private static final int TWO_PAIR = 3;
    private static final int THREE_OF_A_KIND = 4;
    private static final int STRAIGHT = 5;
    private static final int FLUSH = 6;
    private static final int FULL_HOUSE = 7;
    private static final int FOUR_OF_A_KIND = 8;
    private static final int STRAIGHT_FLUSH = 9;
    private static final int ROYAL_FLUSH = 10;

    // Classe interna para representar a força de uma mão
    private static class HandStrength implements Comparable<HandStrength> {
        private final int rankValue;
        private final List<Integer> tieBreakers;

        public HandStrength(int rankValue, List<Integer> tieBreakers) {
            this.rankValue = rankValue;
            this.tieBreakers = tieBreakers;
        }

        @Override
        public int compareTo(HandStrength other) {
            if (this.rankValue != other.rankValue) {
                return Integer.compare(this.rankValue, other.rankValue);
            }
            for (int i = 0; i < Math.min(this.tieBreakers.size(), other.tieBreakers.size()); i++) {
                int compare = Integer.compare(this.tieBreakers.get(i), other.tieBreakers.get(i));
                if (compare != 0)
                    return compare;
            }
            return 0;
        }
    }

    // Avalia o vencedor entre uma lista de jogadores
    public Player evaluateWinner(List<Player> players, List<Card> communityCards) {
        Map<Player, HandStrength> playerStrengths = new HashMap<>();
        for (Player player : players) {
            List<Card> sevenCards = new ArrayList<>(player.getHand());
            sevenCards.addAll(communityCards);
            HandStrength strength = evaluateHand(sevenCards);
            playerStrengths.put(player, strength);
        }

        return Collections.max(playerStrengths.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // Avalia a mão de sete cartas para determinar a melhor combinação possível
    private HandStrength evaluateHand(List<Card> sevenCards) {
        sevenCards.sort(Comparator.comparingInt(this::getCardValue).reversed());

        Map<String, List<Card>> cardsBySuit = sevenCards.stream().collect(Collectors.groupingBy(Card::getSuit));
        Map<Integer, Long> countsByRank = sevenCards.stream()
                .collect(Collectors.groupingBy(Card::getCardValue, Collectors.counting()));

        // Verificar se há um flush e ao mesmo tempo determinar se há um straight flush
        List<Card> flush = findFlush(cardsBySuit);
        List<Card> straight = findStraight(sevenCards);

        if (flush != null && !straight.isEmpty() && straight.get(0).getCardValue() == 14) {
            return new HandStrength(ROYAL_FLUSH, Arrays.asList(14)); // A mão mais alta possível
        }
        if (flush != null && !straight.isEmpty()) {
            return new HandStrength(STRAIGHT_FLUSH, Arrays.asList(straight.get(0).getCardValue()));
        }
        List<Card> fourOfAKind = findNOfAKind(countsByRank, 4, sevenCards);
        if (!fourOfAKind.isEmpty()) {
            return new HandStrength(FOUR_OF_A_KIND, Collections.singletonList(fourOfAKind.get(0).getCardValue()));
        }

        List<Card> threeOfAKind = findNOfAKind(countsByRank, 3, sevenCards);
        List<Card> pair = findNOfAKind(countsByRank, 2, sevenCards);
        if (!threeOfAKind.isEmpty() && !pair.isEmpty()) {
            return new HandStrength(FULL_HOUSE,
                    Arrays.asList(threeOfAKind.get(0).getCardValue(), pair.get(0).getCardValue()));
        }
        if (flush != null) {
            return new HandStrength(FLUSH, flush.stream().map(Card::getCardValue).collect(Collectors.toList()));
        }
        if (!straight.isEmpty()) {
            return new HandStrength(STRAIGHT, Arrays.asList(straight.get(0).getCardValue()));
        }
        if (!threeOfAKind.isEmpty()) {
            return new HandStrength(THREE_OF_A_KIND, Arrays.asList(threeOfAKind.get(0).getCardValue()));
        }
        if (pair.size() >= 2) {
            return new HandStrength(TWO_PAIR, pair.stream().map(Card::getCardValue).sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList()));
        }
        if (!pair.isEmpty()) {
            return new HandStrength(ONE_PAIR, Arrays.asList(pair.get(0).getCardValue()));
        }

        // Retorna a mão de carta alta se nenhuma outra combinação for encontrada
        return new HandStrength(HIGH_CARD,
                sevenCards.stream().map(Card::getCardValue).limit(5).collect(Collectors.toList()));
    }

    // Métodos auxiliares para encontrar combinações específicas
    private List<Card> findFlush(Map<String, List<Card>> cardsBySuit) {
        return cardsBySuit.values().stream().filter(suitCards -> suitCards.size() >= 5).findFirst().orElse(null);
    }

    private List<Card> findStraight(List<Card> cards) {
        List<Integer> cardValues = cards.stream().map(Card::getCardValue).distinct().sorted()
                .collect(Collectors.toList());
        for (int i = 0; i <= cardValues.size() - 5; i++) {
            if (cardValues.get(i) - cardValues.get(i + 4) == 4) {
                int startValue = cardValues.get(i);
                return cards.stream()
                        .filter(card -> card.getCardValue() >= startValue && card.getCardValue() <= startValue + 4)
                        .sorted(Comparator.comparingInt(Card::getCardValue).reversed()).collect(Collectors.toList());
            }
        }
        return Collections.emptyList(); // Retorna uma lista vazia se não encontrar uma sequência
    }

    private List<Card> findNOfAKind(Map<Integer, Long> countsByRank, int n, List<Card> cards) {
        return countsByRank.entrySet().stream()
                .filter(entry -> entry.getValue() == n)
                .flatMap(entry -> cards.stream().filter(card -> card.getCardValue() == entry.getKey()).limit(n))
                .collect(Collectors.toList());
    }

    private int getCardValue(Card card) {
        // Assume que os valores dos ranks são pré-definidos (Ás = 14, Rei = 13, etc.)
        return switch (card.getRank()) {
            case "Ás" -> 14;
            case "Rei" -> 13;
            case "Rainha" -> 12;
            case "Valete" -> 11;
            default -> Integer.parseInt(card.getRank());
        };
    }
}
