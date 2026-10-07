package co.edu.univalle.logic;

import co.edu.univalle.model.Card;
import java.util.List;

/**
 * Controla la lógica del enfrentamiento, cálculo de ATK/DEF y puntajes.
 */
public class Duel {

    private final List<Card> playerDeck;
    private final List<Card> aiDeck;
    private final BattleListener listener;

    private int playerScore = 0;
    private int aiScore = 0;
    private int roundsPlayed = 0;

    public Duel(List<Card> playerDeck, List<Card> aiDeck, BattleListener listener) {
        this.playerDeck = playerDeck;
        this.aiDeck = aiDeck;
        this.listener = listener;
    }

    /**
     * Juega un turno comparando la carta seleccionada por el jugador contra una al azar de la máquina.
     */
    public void playTurn(int playerCardIndex) {
        if (roundsPlayed >= 3 || playerScore == 2 || aiScore == 2) {
            return;
        }

        Card playerCard = playerDeck.get(playerCardIndex);

        // La máquina selecciona una carta al azar entre las disponibles
        int aiCardIndex = (int) (Math.random() * aiDeck.size());
        Card aiCard = aiDeck.get(aiCardIndex);

        listener.onLog("\n--- RONDAS " + (roundsPlayed + 1) + " ---");
        listener.onLog("Tú jugaste: " + playerCard.getName() + " [ATK: " + playerCard.getAtk() + " | DEF: " + playerCard.getDef() + "]");
        listener.onLog("La Máquina jugó: " + aiCard.getName() + " [ATK: " + aiCard.getAtk() + " | DEF: " + aiCard.getDef() + "]");

        String roundWinner;

        // Comparación ATK vs ATK (Ataque simple)
        if (playerCard.getAtk() > aiCard.getAtk()) {
            playerScore++;
            roundWinner = "Jugador";
            listener.onLog("¡Ganaste la ronda! Tu ATK es superior.");
        } else if (aiCard.getAtk() > playerCard.getAtk()) {
            aiScore++;
            roundWinner = "Máquina";
            listener.onLog("La Máquina gana la ronda.");
        } else {
            // Empate: Se compara la DEF como criterio de desempate
            if (playerCard.getDef() > aiCard.getDef()) {
                playerScore++;
                roundWinner = "Jugador (por DEF)";
                listener.onLog("¡Ganaste la ronda por mayor DEF!");
            } else if (aiCard.getDef() > playerCard.getDef()) {
                aiScore++;
                roundWinner = "Máquina (por DEF)";
                listener.onLog("La Máquina gana la ronda por mayor DEF.");
            } else {
                roundWinner = "Empate";
                listener.onLog("¡Empate perfecto en esta ronda! Nadie suma puntos.");
            }
        }

        roundsPlayed++;

        // Notificar eventos a la interfaz
        listener.onTurn(playerCard.getName(), aiCard.getName(), roundWinner);
        listener.onScoreChanged(playerScore, aiScore);

        // Verificar condición de victoria (Primero a 2 victorias o término de 3 rondas)
        if (playerScore == 2 || aiScore == 2 || roundsPlayed == 3) {
            String duelWinner;
            if (playerScore > aiScore) {
                duelWinner = "JUGADOR";
            } else if (aiScore > playerScore) {
                duelWinner = "MÁQUINA";
            } else {
                duelWinner = "EMPATE";
            }

            listener.onLog("\n=========================================");
            listener.onLog("🏆 ¡FIN DEL DUELO! GANADOR: " + duelWinner);
            listener.onLog("=========================================");
            listener.onDuelEnded(duelWinner);
        }
    }
}