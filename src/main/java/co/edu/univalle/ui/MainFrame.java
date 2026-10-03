package co.edu.univalle.ui;

import co.edu.univalle.client.PokeApiClient;
import co.edu.univalle.logic.Battle;
import co.edu.univalle.logic.BattleListener;
import co.edu.univalle.model.Pokemon;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;

public class MainFrame extends JFrame implements BattleListener {

    private final PokeApiClient apiClient = new PokeApiClient();
    private Pokemon pokemon1;
    private Pokemon pokemon2;

    // Componentes del Jugador 1
    private final JTextField txtP1 = new JTextField(10);
    private final JButton btnLoadP1 = new JButton("Load");
    private final JButton btnRandomP1 = new JButton("Random");
    private final JLabel lblImageP1 = new JLabel("Sin imagen", SwingConstants.CENTER);
    private final JLabel lblStatsP1 = new JLabel("Cargue un Pokémon", SwingConstants.CENTER);
    private final JProgressBar hpBarP1 = new JProgressBar();

    // Componentes del Jugador 2
    private final JTextField txtP2 = new JTextField(10);
    private final JButton btnLoadP2 = new JButton("Load");
    private final JButton btnRandomP2 = new JButton("Random");
    private final JLabel lblImageP2 = new JLabel("Sin imagen", SwingConstants.CENTER);
    private final JLabel lblStatsP2 = new JLabel("Cargue un Pokémon", SwingConstants.CENTER);
    private final JProgressBar hpBarP2 = new JProgressBar();

    // Controles de Combate y Log de Batalla
    private final JButton btnFight = new JButton("¡Fight!");
    private final JTextArea txtLog = new JTextArea(12, 50);

    public MainFrame() {
        super("Pokémon Stadium Lite - Univalle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Panel Superior: Selección de Pokémon para J1 y J2
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(createPlayerControlPanel("Jugador 1", txtP1, btnLoadP1, btnRandomP1, true));
        topPanel.add(createPlayerControlPanel("Jugador 2", txtP2, btnLoadP2, btnRandomP2, false));
        add(topPanel, BorderLayout.NORTH);

        // Panel Central: Vista visual de los 2 Pokémon (Sprite, Stats y HP)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(createPokemonDisplayPanel(lblImageP1, lblStatsP1, hpBarP1));
        centerPanel.add(createPokemonDisplayPanel(lblImageP2, lblStatsP2, hpBarP2));
        add(centerPanel, BorderLayout.CENTER);

        // Panel Inferior: Botón de Pelea y Log Desplazable
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        btnFight.setFont(new Font("Arial", Font.BOLD, 18));
        btnFight.setEnabled(false); // Regla: Deshabilitado hasta que ambos estén cargados
        btnFight.addActionListener(e -> startBattle());
        bottomPanel.add(btnFight, BorderLayout.NORTH);

        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        bottomPanel.add(scrollLog, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPlayerControlPanel(String title, JTextField txt, JButton btnLoad, JButton btnRandom, boolean isP1) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(txt);
        panel.add(btnLoad);
        panel.add(btnRandom);

        btnLoad.addActionListener(e -> loadPokemon(txt.getText(), isP1));
        btnRandom.addActionListener(e -> {
            int randomId = new Random().nextInt(1010) + 1; // Selecciona un ID entre 1 y 1010
            loadPokemon(String.valueOf(randomId), isP1);
        });

        return panel;
    }

    private JPanel createPokemonDisplayPanel(JLabel lblImage, JLabel lblStats, JProgressBar hpBar) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        lblImage.setPreferredSize(new Dimension(150, 150));
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(46, 204, 113));

        panel.add(lblStats, BorderLayout.NORTH);
        panel.add(lblImage, BorderLayout.CENTER);
        panel.add(hpBar, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPokemon(String query, boolean isP1) {
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre o ID de un Pokémon", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Uso de SwingWorker para la consulta a la API sin congelar la UI
        new SwingWorker<Pokemon, Void>() {
            @Override
            protected Pokemon doInBackground() throws Exception {
                return apiClient.consultarPokemon(query);
            }

            @Override
            protected void done() {
                try {
                    Pokemon pokemon = get();
                    if (isP1) {
                        pokemon1 = pokemon;
                        updatePokemonUI(pokemon1, lblImageP1, lblStatsP1, hpBarP1);
                    } else {
                        pokemon2 = pokemon;
                        updatePokemonUI(pokemon2, lblImageP2, lblStatsP2, hpBarP2);
                    }

                    // Habilitar el botón de pelea solo si ambos Pokémon están listos
                    btnFight.setEnabled(pokemon1 != null && pokemon2 != null);
                    txtLog.append("Cargado correctamente: " + pokemon.getName().toUpperCase() + "\n");
                } catch (Exception e) {
                    String mensajeError = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(MainFrame.this, mensajeError, "Error al Cargar", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updatePokemonUI(Pokemon p, JLabel lblImage, JLabel lblStats, JProgressBar hpBar) {
        lblStats.setText("<html><center><b>" + p.getName().toUpperCase() + "</b> (" + p.getPrimaryType().toUpperCase() + ")<br>HP: " + p.getMaxHp() + " | ATK: " + p.getAttack() + " | DEF: " + p.getDefense() + " | SPD: " + p.getSpeed() + "</center></html>");

        hpBar.setMaximum(p.getMaxHp());
        hpBar.setValue(p.getCurrentHp());
        hpBar.setString(p.getCurrentHp() + " / " + p.getMaxHp() + " HP");

        // Cargar imagen de forma asíncrona (utilizando la lógica revisada en clase)
        if (p.getSpriteUrl() != null && !p.getSpriteUrl().isEmpty()) {
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    URL url = new URL(p.getSpriteUrl());
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }

                @Override
                protected void done() {
                    try {
                        lblImage.setText("");
                        lblImage.setIcon(get());
                    } catch (Exception e) {
                        lblImage.setIcon(null);
                        lblImage.setText("Sin imagen");
                    }
                }
            }.execute();
        }
    }

    private void startBattle() {
        btnFight.setEnabled(false);
        btnLoadP1.setEnabled(false);
        btnRandomP1.setEnabled(false);
        btnLoadP2.setEnabled(false);
        btnRandomP2.setEnabled(false);

        txtLog.setText("=== ¡COMIENZA EL COMBATE! ===\n");
        Battle battle = new Battle(pokemon1, pokemon2, this);
        battle.startBattle();
    }

    // --- IMPLEMENTACIÓN DE LOS EVENTOS DE BATTLE LISTENER ---

    @Override
    public void onTurn(String attacker, String defender, int damage, boolean critical, double modifier) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("⚔️ ").append(attacker.toUpperCase()).append(" ataca a ").append(defender.toUpperCase());
            sb.append(" causando ").append(damage).append(" de daño.");
            if (critical) sb.append(" ¡GOLPE CRÍTICO!");
            if (modifier > 1.0) sb.append(" ¡Es muy efectivo!");
            if (modifier < 1.0) sb.append(" No es muy efectivo...");
            sb.append("\n");

            txtLog.append(sb.toString());
        });
    }

    @Override
    public void onHpChanged(String pokemonName, int hpActual) {
        SwingUtilities.invokeLater(() -> {
            if (pokemon1 != null && pokemon1.getName().equalsIgnoreCase(pokemonName)) {
                hpBarP1.setValue(hpActual);
                hpBarP1.setString(hpActual + " / " + pokemon1.getMaxHp() + " HP");
            } else if (pokemon2 != null && pokemon2.getName().equalsIgnoreCase(pokemonName)) {
                hpBarP2.setValue(hpActual);
                hpBarP2.setString(hpActual + " / " + pokemon2.getMaxHp() + " HP");
            }
        });
    }

    @Override
    public void onBattleEnded(String winnerName) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append("\n=========================================\n");
            txtLog.append("🏆 ¡EL GANADOR ES " + winnerName.toUpperCase() + "! 🎉\n");
            txtLog.append("=========================================\n");

            btnLoadP1.setEnabled(true);
            btnRandomP1.setEnabled(true);
            btnLoadP2.setEnabled(true);
            btnRandomP2.setEnabled(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}