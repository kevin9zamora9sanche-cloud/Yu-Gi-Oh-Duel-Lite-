package co.edu.univalle.logic;

public interface BattleListener {
    void onLog(String message);
    void onHpUpdated(String pokemonName, int currentHp, int maxHp);
    void onBattleEnded(String winnerName);
}