package co.edu.univalle.ui;

// Importaciones del modelo, lógica y cliente de datos
import co.edu.univalle.client.PokeApiService;
import co.edu.univalle.logic.BattleEngine;
import co.edu.univalle.logic.BattleListener;
import co.edu.univalle.model.Pokemon;

// Importaciones de Swing y AWT para la interfaz gráfica
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URL;

/**
 * Ventana principal del simulador "Pokémon Stadium Lite".
 * Gestiona la interfaz de usuario y reacciona a los eventos del combate mediante BattleListener.
 */
public class MainFrame extends JFrame implements BattleListener {

    // =========================================================================
    // ATRIBUTOS DE LA INTERFAZ (Enlazados con el diseñador Swing / MainFrame.form)
    // =========================================================================

    // Paneles contenedores de la vista
    private JPanel mainPanel;         // Panel raíz principal
    private JPanel centerPanel;       // Contenedor de la zona central
    private JPanel p1CenterPanel;     // Panel visual del Jugador 1
    private JPanel p2CenterPanel;     // Panel visual del Jugador 2
    private JPanel bottomPanel;       // Panel inferior para controles y logs
    private JScrollPane scrollPaneLog; // Scroll para el área de texto de registro

    // Componentes interactivos del Jugador 1
    private JLabel lblStatsP1;        // Muestra las estadísticas (HP, ATK, DEF, SPD)
    private JLabel lblImageP1;        // Muestra el sprite/imagen del Pokémon
    private JProgressBar hpBarP1;     // Barra de vida (HP)
    private JTextField txtP1;         // Campo de entrada de nombre o ID
    private JButton btnLoadP1;        // Botón para cargar Pokémon por nombre/ID
    private JButton btnRandomP1;      // Botón para cargar un Pokémon aleatorio

    // Componentes interactivos del Jugador 2
    private JLabel lblStatsP2;
    private JLabel lblImageP2;
    private JProgressBar hpBarP2;
    private JTextField txtP2;
    private JButton btnLoadP2;
    private JButton btnRandomP2;

    // Controles de batalla y registro de sucesos
    private JButton btnFight;         // Botón para iniciar el combate
    private JTextArea txtLog;         // Registro textual de los turnos y eventos

    // =========================================================================
    // SERVICIOS Y OBJETOS DEL DOMINIO
    // =========================================================================
    private PokeApiService apiService; // Servicio para consultar la PokéAPI en segundo plano
    private Pokemon pokemon1;          // Objeto del Pokémon asignado al Jugador 1
    private Pokemon pokemon2;          // Objeto del Pokémon asignado al Jugador 2

    /**
     * Constructor principal de la ventana.
     * Configura el panel contenedor, inicializa el servicio API y enlaza los eventos.
     */
    public MainFrame() {
        setTitle("Pokémon Stadium Lite - Univalle");

        // Validación de seguridad por si el diseñador visual no inicializa el panel raíz
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();                       // Ajusta el tamaño de la ventana al contenido
        setLocationRelativeTo(null);   // Centra la ventana en la pantalla

        apiService = new PokeApiService();
        setupEvents();                 // Registra los escuchadores de eventos
    }

    /**
     * Asigna las acciones (listeners) a los botones de la interfaz.
     */
    private void setupEvents() {
        // Carga manual de Pokémon mediante el texto ingresado
        btnLoadP1.addActionListener(e -> cargarPokemon1(txtP1.getText()));
        btnLoadP2.addActionListener(e -> cargarPokemon2(txtP2.getText()));

        // Carga aleatoria utilizando IDs en el rango de la 1ª a la 8ª generación (1 a 898)
        btnRandomP1.addActionListener(e -> {
            int randomId = (int) (Math.random() * 898) + 1;
            cargarPokemon1(String.valueOf(randomId));
        });

        btnRandomP2.addActionListener(e -> {
            int randomId = (int) (Math.random() * 898) + 1;
            cargarPokemon2(String.valueOf(randomId));
        });

        // Inicio del combate automático
        btnFight.addActionListener(e -> {
            if (pokemon1 != null && pokemon2 != null) {
                // Deshabilitar controles interactivos durante la pelea para evitar interferencias
                btnFight.setEnabled(false);
                btnLoadP1.setEnabled(false);
                btnRandomP1.setEnabled(false);
                btnLoadP2.setEnabled(false);
                btnRandomP2.setEnabled(false);

                txtLog.setText(""); // Limpiar la consola de logs

                // Restablecer la vida de ambos combatientes al máximo antes de iniciar
                pokemon1.setCurrentHp(pokemon1.getMaxHp());
                pokemon2.setCurrentHp(pokemon2.getMaxHp());
                actualizarBarra(hpBarP1, pokemon1);
                actualizarBarra(hpBarP2, pokemon2);

                // Iniciar el motor de batalla en un hilo separado
                BattleEngine engine = new BattleEngine(pokemon1, pokemon2, this);
                engine.startBattle();
            } else {
                JOptionPane.showMessageDialog(this, "Cargue ambos Pokémon antes de iniciar la batalla.");
            }
        });
    }

    /**
     * Habilita el botón de combate si ambos combatientes han sido cargados exitosamente.
     */
    private void verificarEstadoBotonPelea() {
        if (pokemon1 != null && pokemon2 != null) {
            btnFight.setEnabled(true);
        }
    }

    /**
     * Consulta el Pokémon del Jugador 1 en la API y actualiza la vista.
     */
    private void cargarPokemon1(String query) {
        Pokemon p = apiService.obtenerPokemon(query);
        if (p != null) {
            pokemon1 = p;
            txtP1.setText(p.getName());
            actualizarUI(pokemon1, lblImageP1, lblStatsP1, hpBarP1);
            txtLog.append("¡Cargado correctamente: " + p.getName().toUpperCase() + "!\n");
            verificarEstadoBotonPelea();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el Pokémon en la PokéAPI.");
        }
    }

    /**
     * Consulta el Pokémon del Jugador 2 en la API y actualiza la vista.
     */
    private void cargarPokemon2(String query) {
        Pokemon p = apiService.obtenerPokemon(query);
        if (p != null) {
            pokemon2 = p;
            txtP2.setText(p.getName());
            actualizarUI(pokemon2, lblImageP2, lblStatsP2, hpBarP2);
            txtLog.append("¡Cargado correctamente: " + p.getName().toUpperCase() + "!\n");
            verificarEstadoBotonPelea();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el Pokémon en la PokéAPI.");
        }
    }

    /**
     * Actualiza las etiquetas de texto e imagen de un Pokémon en la interfaz.
     */
    private void actualizarUI(Pokemon p, JLabel lblImage, JLabel lblStats, JProgressBar hpBar) {
        // Formatear texto con código HTML para centrar y resaltar datos
        lblStats.setText("<html><center><b>" + p.getName().toUpperCase() + " (" + p.getPrimaryType().toUpperCase() + ")</b><br>" +
                "HP: " + p.getMaxHp() + " | ATK: " + p.getAttack() + " | DEF: " + p.getDefense() + " | SPD: " + p.getSpeed() + "</center></html>");

        actualizarBarra(hpBar, p);

        // Descarga y renderizado dinámico de la imagen del sprite desde URL
        if (p.getSpriteUrl() != null && !p.getSpriteUrl().isEmpty()) {
            try {
                URL urlImagen = new URL(p.getSpriteUrl());
                ImageIcon icono = new ImageIcon(urlImagen);
                Image image = icono.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblImage.setText("");
                lblImage.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                lblImage.setIcon(null);
                lblImage.setText("Sin imagen");
            }
        }
    }

    /**
     * Configura y actualiza el valor visual de la barra de vida (JProgressBar).
     */
    private void actualizarBarra(JProgressBar hpBar, Pokemon p) {
        hpBar.setMaximum(p.getMaxHp());
        hpBar.setValue(p.getCurrentHp());
        hpBar.setStringPainted(true);
        hpBar.setString(p.getCurrentHp() + " / " + p.getMaxHp() + " HP");
        hpBar.setForeground(new Color(46, 204, 113)); // Color verde
    }

    // =========================================================================
    // MÉTODOS DE LA INTERFAZ BattleListener (Sincronización de hilos con AWT/Swing)
    // =========================================================================

    /**
     * Recibe los mensajes del motor de batalla e imprime en la consola de texto.
     */
    @Override
    public void onLog(String message) {
        // SwingUtilities.invokeLater garantiza que los cambios a la UI se ejecuten en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> txtLog.append(message + "\n"));
    }

    /**
     * Recibe la actualización del HP de un Pokémon tras recibir un ataque.
     */
    @Override
    public void onHpUpdated(Pokemon target, int currentHp, int maxHp) {
        SwingUtilities.invokeLater(() -> {
            // Comparación por referencia de memoria (==) en lugar de nombre para diferenciar Pokémon idénticos
            if (target == pokemon1) {
                actualizarBarra(hpBarP1, pokemon1);
            } else if (target == pokemon2) {
                actualizarBarra(hpBarP2, pokemon2);
            }
        });
    }
    /**
     * Se invoca cuando finaliza el combate para volver a habilitar la interfaz.
     */
    @Override
    public void onBattleEnded(String winnerName) {
        SwingUtilities.invokeLater(() -> {
            btnLoadP1.setEnabled(true);
            btnRandomP1.setEnabled(true);
            btnLoadP2.setEnabled(true);
            btnRandomP2.setEnabled(true);
            btnFight.setEnabled(true);
        });
    }

    /**
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setEnabled(false);
        mainPanel.setMinimumSize(new Dimension(800, 600));
        mainPanel.setPreferredSize(new Dimension(800, 600));
        centerPanel = new JPanel();
        centerPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        centerPanel.setEnabled(false);
        mainPanel.add(centerPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        p1CenterPanel = new JPanel();
        p1CenterPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        p1CenterPanel.setEnabled(false);
        centerPanel.add(p1CenterPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        p1CenterPanel.setBorder(BorderFactory.createTitledBorder(null, "Jugador 1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblStatsP1 = new JLabel();
        lblStatsP1.setText("Cargue un Pokémon");
        p1CenterPanel.add(lblStatsP1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblImageP1 = new JLabel();
        lblImageP1.setText("Sin imagen");
        p1CenterPanel.add(lblImageP1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hpBarP1 = new JProgressBar();
        p1CenterPanel.add(hpBarP1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtP1 = new JTextField();
        p1CenterPanel.add(txtP1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(150, -1), null, 0, false));
        btnLoadP1 = new JButton();
        btnLoadP1.setText("Load");
        p1CenterPanel.add(btnLoadP1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        btnRandomP1 = new JButton();
        btnRandomP1.setText("Random");
        p1CenterPanel.add(btnRandomP1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        p2CenterPanel = new JPanel();
        p2CenterPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        centerPanel.add(p2CenterPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        p2CenterPanel.setBorder(BorderFactory.createTitledBorder(null, "Jugador 2", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblStatsP2 = new JLabel();
        lblStatsP2.setText("Cargue un Pokémon");
        p2CenterPanel.add(lblStatsP2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblImageP2 = new JLabel();
        lblImageP2.setText("Sin imagen");
        p2CenterPanel.add(lblImageP2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hpBarP2 = new JProgressBar();
        p2CenterPanel.add(hpBarP2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtP2 = new JTextField();
        p2CenterPanel.add(txtP2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(150, -1), null, 0, false));
        btnLoadP2 = new JButton();
        btnLoadP2.setText("Load");
        p2CenterPanel.add(btnLoadP2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        btnRandomP2 = new JButton();
        btnRandomP2.setText("Random");
        p2CenterPanel.add(btnRandomP2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(bottomPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnFight = new JButton();
        btnFight.setEnabled(false);
        btnFight.setText("¡Fight!");
        bottomPanel.add(btnFight, BorderLayout.NORTH);
        scrollPaneLog = new JScrollPane();
        bottomPanel.add(scrollPaneLog, BorderLayout.CENTER);
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setEnabled(false);
        scrollPaneLog.setViewportView(txtLog);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}