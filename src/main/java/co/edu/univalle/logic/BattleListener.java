package co.edu.univalle.logic;

public interface BattleListener {
    // Evento notificado en cada turno del combate
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);

    // Evento para actualizar la barra/indicador de vida
    void onHpChanged(String pokemonName, int hpActual);

    // Evento al finalizar el combate con el ganador
    void onBattleEnded(String winnerName);
}