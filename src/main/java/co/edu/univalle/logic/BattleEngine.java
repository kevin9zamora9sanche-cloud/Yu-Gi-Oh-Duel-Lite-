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

            // REGLA: Si la velocidad es igual, se elige al azar quién inicia
            Pokemon attacker;
            Pokemon defender;

            if (p1.getSpeed() > p2.getSpeed()) {
                attacker = p1;
                defender = p2;
            } else if (p2.getSpeed() > p1.getSpeed()) {
                attacker = p2;
                defender = p1;
            } else {
                // Misma velocidad (e.g. espejo o misma especie): Selección aleatoria de turno 1
                attacker = (Math.random() < 0.5) ? p1 : p2;
                defender = (attacker == p1) ? p2 : p1;
            }

            while (!p1.isFainted() && !p2.isFainted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Cálculo de daño
                int damage = Math.max(5, attacker.getAttack() - (defender.getDefense() / 2));

                boolean isCritical = Math.random() < 0.15;
                if (isCritical) {
                    damage = (int)(damage * 1.5);
                }

                defender.setCurrentHp(defender.getCurrentHp() - damage);

                String criticalMsg = isCritical ? " ¡GOLPE CRÍTICO!" : "";
                listener.onLog("⚔ " + attacker.getName().toUpperCase() + " ataca causando " + damage + " de daño." + criticalMsg);

                // Pasamos la referencia del objeto 'defender' para distinguir duplicados
                listener.onHpUpdated(defender, defender.getCurrentHp(), defender.getMaxHp());

                // Verificación de derrota
                if (defender.isFainted()) {
                    listener.onLog("\n=========================================");
                    listener.onLog("🏆 ¡EL GANADOR ES " + attacker.getName().toUpperCase() + "!");
                    listener.onLog("=========================================");
                    listener.onBattleEnded(attacker.getName());
                    break;
                }

                // Cambio de turno
                Pokemon temp = attacker;
                attacker = defender;
                defender = temp;
            }
        }).start();
    }
}