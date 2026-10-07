package co.edu.univalle.ui;

import co.edu.univalle.client.YgoApiClient;
import co.edu.univalle.logic.BattleListener;
import co.edu.univalle.logic.Duel;
import co.edu.univalle.model.Card;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements BattleListener {

    private JPanel mainPanel;
    private JPanel cardsPanel;
    private JPanel p1Panel;
    private JPanel aiPanel;

    private JLabel[] lblPlayerCardImages = new JLabel[3];
    private JLabel[] lblPlayerCardStats = new JLabel[3];
    private JButton[] btnSelectCards = new JButton[3];

    private JLabel[] lblAiCardImages = new JLabel[3];
    private JLabel[] lblAiCardStats = new JLabel[3];

    private JLabel lblScore;
    private JButton btnStartDuel;
    private JTextArea txtLog;

    private YgoApiClient apiClient;
    private List<Card> playerDeck = new ArrayList<>();
    private List<Card> aiDeck = new ArrayList<>();
    private Duel duel;

    public MainFrame() {
        setTitle("Yu-Gi-Oh! Duel Lite - Univalle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        apiClient = new YgoApiClient();
        initCustomUI();
        cargarMazoInicial();
    }

    private void initCustomUI() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        // Panel de marcadores y control
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        lblScore = new JLabel("Puntaje: Jugador 0 - 0 Máquina");
        lblScore.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnStartDuel = new JButton("Cargar / Iniciar Duelo");
        btnStartDuel.addActionListener(e -> cargarMazoInicial());
        topPanel.add(lblScore);
        topPanel.add(btnStartDuel);

        // Centro: Cartas
        cardsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Panel Jugador
        p1Panel = new JPanel(new GridLayout(1, 3, 5, 5));
        p1Panel.setBorder(BorderFactory.createTitledBorder(null, "Tus Cartas", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 14)));

        for (int i = 0; i < 3; i++) {
            JPanel cardContainer = new JPanel(new BorderLayout(5, 5));
            lblPlayerCardStats[i] = new JLabel("Cargando...", SwingConstants.CENTER);
            lblPlayerCardImages[i] = new JLabel("Sin imagen", SwingConstants.CENTER);
            btnSelectCards[i] = new JButton("Jugar Carta");
            btnSelectCards[i].setEnabled(false);

            int index = i;
            btnSelectCards[i].addActionListener(e -> {
                btnSelectCards[index].setEnabled(false);
                if (duel != null) {
                    duel.playTurn(index);
                }
            });

            cardContainer.add(lblPlayerCardStats[i], BorderLayout.NORTH);
            cardContainer.add(lblPlayerCardImages[i], BorderLayout.CENTER);
            cardContainer.add(btnSelectCards[i], BorderLayout.SOUTH);
            p1Panel.add(cardContainer);
        }

        // Panel Máquina
        aiPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        aiPanel.setBorder(BorderFactory.createTitledBorder(null, "Cartas de la Máquina", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 14)));

        for (int i = 0; i < 3; i++) {
            JPanel cardContainer = new JPanel(new BorderLayout(5, 5));
            lblAiCardStats[i] = new JLabel("Oculto", SwingConstants.CENTER);
            lblAiCardImages[i] = new JLabel("🎴 Oculta", SwingConstants.CENTER);

            cardContainer.add(lblAiCardStats[i], BorderLayout.NORTH);
            cardContainer.add(lblAiCardImages[i], BorderLayout.CENTER);
            aiPanel.add(cardContainer);
        }

        cardsPanel.add(p1Panel);
        cardsPanel.add(aiPanel);

        // Parte inferior: Log de eventos
        txtLog = new JTextArea(8, 80);
        txtLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLog);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        mainPanel.add(scrollLog, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void cargarMazoInicial() {
        btnStartDuel.setEnabled(false);
        txtLog.setText("Obteniendo cartas Monster desde YGOProDeck API...\n");

        for (int i = 0; i < 3; i++) {
            btnSelectCards[i].setEnabled(false);
            lblPlayerCardStats[i].setText("Cargando...");
            lblPlayerCardImages[i].setIcon(null);
            lblPlayerCardImages[i].setText("Cargando...");

            lblAiCardStats[i].setText("Oculto");
            lblAiCardImages[i].setIcon(null);
            lblAiCardImages[i].setText("🎴 Oculta");
        }

        // Hilo en segundo plano para no congelar la UI
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
                        btnSelectCards[i].setEnabled(true);
                    }
                    txtLog.append("¡Cartas cargadas con éxito! Elige una carta para iniciar.\n");
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
        lblPlayerCardStats[index].setText("<html><center><b>" + card.getName() + "</b><br>ATK: " + card.getAtk() + " | DEF: " + card.getDef() + "</center></html>");
        new Thread(() -> {
            try {
                URL url = new URL(card.getImageUrl());
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    lblPlayerCardImages[index].setText("");
                    lblPlayerCardImages[index].setIcon(new ImageIcon(img));
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> lblPlayerCardImages[index].setText("Sin imagen"));
            }
        }).start();
    }

    @Override
    public void onLog(String message) {
        SwingUtilities.invokeLater(() -> txtLog.append(message + "\n"));
    }

    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        // Notificación opcional de turno
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        SwingUtilities.invokeLater(() -> lblScore.setText("Puntaje: Jugador " + playerScore + " - " + aiScore + " Máquina"));
    }

    @Override
    public void onDuelEnded(String winner) {
        SwingUtilities.invokeLater(() -> {
            for (JButton btn : btnSelectCards) {
                btn.setEnabled(false);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}