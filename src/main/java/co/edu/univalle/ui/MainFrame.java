package co.edu.univalle.ui;

import co.edu.univalle.client.YgoApiClient;
import co.edu.univalle.logic.BattleListener;
import co.edu.univalle.logic.Duel;
import co.edu.univalle.model.Card;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements BattleListener {

    // Componentes vinculados con el .form
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel p1Panel;
    private JPanel p2Panel;
    private JPanel bottomPanel;
    private JLabel lblScore;
    private JButton btnStartDuel;
    private JTextArea txtLog;

    // Componentes del Jugador 1 (enlazados uno a uno desde el form)
    private JLabel lblPlayerStats1, lblPlayerStats2, lblPlayerStats3;
    private JLabel lblPlayerImg1, lblPlayerImg2, lblPlayerImg3;
    private JButton btnPlay1, btnPlay2, btnPlay3;

    // Componentes de la Máquina (enlazados uno a uno desde el form)
    private JLabel lblAiStats1, lblAiStats2, lblAiStats3;
    private JLabel lblAiImg1, lblAiImg2, lblAiImg3;

    // Estructuras auxiliares en código
    private JLabel[] playerStatsLabels;
    private JLabel[] playerImgLabels;
    private JButton[] playerButtons;

    private JLabel[] aiStatsLabels;
    private JLabel[] aiImgLabels;

    private YgoApiClient apiClient;
    private List<Card> playerDeck = new ArrayList<>();
    private List<Card> aiDeck = new ArrayList<>();
    private Duel duel;

    public MainFrame() {
        setTitle("Yu-Gi-Oh! Duel Lite - Univalle");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        apiClient = new YgoApiClient();

        // Agrupar referencias en arreglos para iterar fácilmente
        playerStatsLabels = new JLabel[]{lblPlayerStats1, lblPlayerStats2, lblPlayerStats3};
        playerImgLabels = new JLabel[]{lblPlayerImg1, lblPlayerImg2, lblPlayerImg3};
        playerButtons = new JButton[]{btnPlay1, btnPlay2, btnPlay3};

        aiStatsLabels = new JLabel[]{lblAiStats1, lblAiStats2, lblAiStats3};
        aiImgLabels = new JLabel[]{lblAiImg1, lblAiImg2, lblAiImg3};

        setupEvents();
        cargarMazoInicial();
    }

    private void setupEvents() {
        btnStartDuel.addActionListener(e -> cargarMazoInicial());

        for (int i = 0; i < 3; i++) {
            int index = i;
            playerButtons[i].addActionListener(e -> {
                playerButtons[index].setEnabled(false);
                if (duel != null) {
                    duel.playTurn(index);
                }
            });
        }
    }

    private void cargarMazoInicial() {
        btnStartDuel.setEnabled(false);
        txtLog.setText("Obteniendo cartas Monster desde YGOProDeck API...\n");

        for (int i = 0; i < 3; i++) {
            playerButtons[i].setEnabled(false);
            playerStatsLabels[i].setText("Cargando...");
            playerImgLabels[i].setIcon(null);
            playerImgLabels[i].setText("Cargando...");

            aiStatsLabels[i].setText("Oculto");
            aiImgLabels[i].setIcon(null);
            aiImgLabels[i].setText("🎴 Oculta");
        }

        new Thread(() -> {
            playerDeck.clear();
            aiDeck.clear();

            for (int i = 0; i < 3; i++) {
                Card pCard = apiClient.obtenerCartaMonsterAleatoria();
                Card aiCard = apiClient.obtenerCartaMonsterAleatoria();

                if (pCard != null) playerDeck.add(pCard);
                if (aiCard != null) aiDeck.add(aiCard);
            }

            SwingUtilities.invokeLater(() -> {
                if (playerDeck.size() == 3 && aiDeck.size() == 3) {
                    for (int i = 0; i < 3; i++) {
                        mostrarCartaJugador(i, playerDeck.get(i));
                        playerButtons[i].setEnabled(true);
                    }
                    txtLog.append("¡Cartas cargadas con éxito! Elige una carta para jugar la ronda.\n");
                    lblScore.setText("Puntaje: Jugador 0 - 0 Máquina");
                    duel = new Duel(playerDeck, aiDeck, this);
                } else {
                    txtLog.append("Error al obtener cartas de la API. Intenta nuevamente.\n");
                    JOptionPane.showMessageDialog(this, "Error de red al conectar con la API.");
                }
                btnStartDuel.setEnabled(true);
            });
        }).start();
    }

    private void mostrarCartaJugador(int index, Card card) {
        playerStatsLabels[index].setText("<html><center><b>" + card.getNombre() + "</b><br>ATK: " + card.getAtk() + " | DEF: " + card.getDef() + "</center></html>");
        new Thread(() -> {
            try {
                URL url = new URL(card.getImageUrl());
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    playerImgLabels[index].setText("");
                    playerImgLabels[index].setIcon(new ImageIcon(img));
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> playerImgLabels[index].setText("Sin imagen"));
            }
        }).start();
    }

    @Override
    public void onLog(String message) {
        SwingUtilities.invokeLater(() -> txtLog.append(message + "\n"));
    }

    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        SwingUtilities.invokeLater(() -> lblScore.setText("Puntaje: Jugador " + playerScore + " - " + aiScore + " Máquina"));
    }

    @Override
    public void onDuelEnded(String winner) {
        SwingUtilities.invokeLater(() -> {
            for (JButton btn : playerButtons) {
                btn.setEnabled(false);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}