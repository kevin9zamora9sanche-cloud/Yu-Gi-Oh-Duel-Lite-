package co.edu.univalle.logic;

import co.edu.univalle.model.Pokemon;

public class BattleEngine {

    private Pokemon p1;
    private Pokemon p2;
    private BattleListener listener;

    public BattleEngine(Pokemon p1, Pokemon p2, BattleListener listener) {
        this.p1 = p1;
        this.p2 = p2;
        this.listener = listener;
    }

    public void startBattle() {
        new Thread(() -> {
            listener.onLog("=== ¡COMIENZA EL COMBATE! ===");

            Pokemon attacker = (p1.getSpeed() >= p2.getSpeed()) ? p1 : p2;
            Pokemon defender = (attacker == p1) ? p2 : p1;

            while (!p1.isFainted() && !p2.isFainted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int damage = Math.max(5, attacker.getAttack() - (defender.getDefense() / 2));

                boolean isCritical = Math.random() < 0.15;
                if (isCritical) {
                    damage = (int)(damage * 1.5);
                }

                defender.setCurrentHp(defender.getCurrentHp() - damage);

                String criticalMsg = isCritical ? " ¡GOLPE CRITICO!" : "";
                listener.onLog("⚔ " + attacker.getName().toUpperCase() + " ataca a " + defender.getName().toUpperCase() + " causando " + damage + " de daño." + criticalMsg);
                listener.onHpUpdated(defender.getName(), defender.getCurrentHp(), defender.getMaxHp());

                if (defender.isFainted()) {
                    listener.onLog("\n=========================================");
                    listener.onLog("🏆 ¡EL GANADOR ES " + attacker.getName().toUpperCase() + "!");
                    listener.onLog("=========================================");
                    listener.onBattleEnded(attacker.getName());
                    break;
                }

                Pokemon temp = attacker;
                attacker = defender;
                defender = temp;
            }
        }).start();
    }
}