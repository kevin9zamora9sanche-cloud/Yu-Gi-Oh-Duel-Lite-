package co.edu.univalle.logic;

import co.edu.univalle.model.Pokemon;

public interface BattleListener {
    void onLog(String message);
    void onHpUpdated(Pokemon target, int currentHp, int maxHp); // <-- Recibe el objeto
    void onBattleEnded(String winnerName);
    //void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);
}