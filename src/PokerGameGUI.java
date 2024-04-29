import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PokerGameGUI extends JFrame {
    private PokerGame game;
    private JTextArea[] playerHands;
    private JTextArea communityCardsArea;
    private JButton btnDeal, btnFold, btnBet, btnRestart, btnShowChips;
    private JTextField betAmountField;
    private JLabel potLabel;
    private PokerTablePanel tablePanel;

    public PokerGameGUI(PokerGame game) {
        this.game = game;
        initializeUI();
        game.setGameGUI(this);
    }

    private void initializeUI() {
        setTitle("Texas Hold'em Poker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Substitui o JPanel padrão por PokerTablePanel
        tablePanel = new PokerTablePanel(game.getPlayers());
        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        communityCardsArea = new JTextArea(3, 50);
        communityCardsArea.setEditable(false);
        add(communityCardsArea, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        btnDeal = new JButton("Deal");
        btnFold = new JButton("Fold");
        btnBet = new JButton("Bet");
        btnRestart = new JButton("Restart Game");
        btnShowChips = new JButton("Show Chips");
        betAmountField = new JTextField(5);

        btnDeal.addActionListener(this::dealAction);
        btnFold.addActionListener(this::foldAction);
        btnBet.addActionListener(this::betAction);
        btnRestart.addActionListener(e -> game.restartGame());
        btnShowChips.addActionListener(e -> displayPlayerChips());

        potLabel = new JLabel("Pot: " + game.getPot());
        bottomPanel.add(potLabel);
        bottomPanel.add(btnDeal);
        bottomPanel.add(btnFold);
        bottomPanel.add(btnBet);
        bottomPanel.add(new JLabel("Bet Amount:"));
        bottomPanel.add(betAmountField);
        bottomPanel.add(btnRestart);
        bottomPanel.add(btnShowChips);

        add(bottomPanel, BorderLayout.NORTH);

        // Initialize playerHands array
        playerHands = new JTextArea[game.getPlayers().size()];
        for (int i = 0; i < playerHands.length; i++) {
            playerHands[i] = new JTextArea(5, 15);
            playerHands[i].setEditable(false);
            // Not adding these JTextAreas to the GUI here because the table panel will handle player display
        }

        setVisible(true);
    }

    private void dealAction(ActionEvent e) {
        game.dealNextCards();
        updateUI();
    }

    private void foldAction(ActionEvent e) {
        game.getCurrentPlayer().fold();
        updateUI();
    }

    private void betAction(ActionEvent e) {
        int amount = Integer.parseInt(betAmountField.getText());
        game.getCurrentPlayer().bet(amount);
        game.addToPot(amount);
        updateUI();
    }

    void updateUI() {
        communityCardsArea.setText(game.getCommunityCardString());
        for (int i = 0; i < game.getPlayers().size(); i++) {
            playerHands[i].setText(game.getPlayers().get(i).getHandString());
        }
        potLabel.setText("Pot: " + game.getPot());
        tablePanel.setCurrentPlayerIndex(game.getCurrentPlayerIndex());
        repaint();  // Redraw the GUI to show current player highlight
    }

    public void displayPlayerChips() {
        StringBuilder chipsInfo = new StringBuilder("Fichas dos Jogadores:\n");
        for (Player player : game.getPlayers()) {
            chipsInfo.append(player.getName()).append(": ").append(player.getChips()).append(" fichas\n");
        }
        JOptionPane.showMessageDialog(this, chipsInfo.toString());
    }

    public void showWinner(Player winner) {
        // Verifica se há um vencedor e exibe sua mão.
        if (winner != null) {
            JOptionPane.showMessageDialog(this,
                winner.getName() + " wins with " + winner.getHandString(),
                "Game Winner",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No winner in this round",
                "Game Winner",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    

    class PokerTablePanel extends JPanel {
        List<Player> players;
        int currentPlayerIndex = -1;

        public PokerTablePanel(List<Player> players) {
            this.players = players;
            setPreferredSize(new Dimension(600, 400));
        }

        public void setCurrentPlayerIndex(int index) {
            this.currentPlayerIndex = index;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(0, 128, 0)); // Green color for the table
            g.fillOval(50, 50, getWidth() - 100, getHeight() - 100); // Draws an oval table

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(getWidth(), getHeight()) / 3;
            double angleStep = 2 * Math.PI / players.size();

            for (int i = 0; i < players.size(); i++) {
                int x = (int) (centerX + radius * Math.cos(angleStep * i)) - 50;
                int y = (int) (centerY + radius * Math.sin(angleStep * i)) - 30;
                if (i == currentPlayerIndex) {
                    g.setColor(Color.BLACK); // Highlight the current player
                } else {
                    g.setColor(new Color(0, 128, 0)); // Normal color for other players
                }
                g.fillOval(x, y, 100, 60); // Draw the player spot
                g.setColor(Color.WHITE);
                g.drawString(players.get(i).getName() + ": " + players.get(i).getChips(), x + 10, y + 30);
            }
        }
    }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try {
            // Configura a aparência da interface para seguir o estilo do sistema operacional
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lista de nomes de jogadores - pode ser modificada para incluir entradas do usuário
        List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie", "Diana");

        // Cada jogador começa com uma quantia inicial de fichas
        int initialChips = 1000;

        // Cria a instância do jogo
        PokerGame game = new PokerGame(playerNames, initialChips);

        // Cria e exibe a GUI
        new PokerGameGUI(game);
    });
}

}
