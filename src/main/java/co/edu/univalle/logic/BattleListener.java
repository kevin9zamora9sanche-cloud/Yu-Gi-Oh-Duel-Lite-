package co.edu.univalle.logic;

/**
 * Escuchador de eventos del duelo para desacoplar la lógica de la UI.
 */
public interface BattleListener {
    void onLog(String message);
    void onTurn(String playerCard, String aiCard, String winner);
    void onScoreChanged(int playerScore, int aiScore);
    void onDuelEnded(String winner);
}