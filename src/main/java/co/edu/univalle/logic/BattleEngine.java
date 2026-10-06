package co.edu.univalle.logic;

import co.edu.univalle.model.Pokemon;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BattleEngine {

    private Pokemon p1;
    private Pokemon p2;
    private BattleListener listener;

    private static final double CRIT_CHANCE = 0.10;
    private static final double CRIT_MULTIPLIER = 1.5;

    private Random random = new Random();

    // Efectividad simple: clave = "tipoAtacante-tipoDefensor". Lo que no esté aquí vale 1.0.
    private static final Map<String, Double> EFFECTIVENESS = Map.of(
            "water-fire", 1.3,
            "fire-grass", 1.3,
            "grass-water", 1.3,
            "fire-water", 0.7,
            "grass-fire", 0.7,
            "water-grass", 0.7
    );
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
                boolean isCritical = random.nextDouble() < CRIT_CHANCE;
                double effectiviness = calcEffectiveness(attacker.getPrimaryType(), defender.getPrimaryType());
                int damage = calcDamage(attacker, defender, isCritical, effectiviness);

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

    private int calcDamage(Pokemon attacker, Pokemon defender, boolean isCritic, double effectiveness)
    {
        int attackerDamage = attacker.getAttack();
        int defenderDefense = defender.getDefense();

        // base: ATK * (ATK / (ATK + DEF))
        double damage = attackerDamage * ( (double) attackerDamage / (attackerDamage + defenderDefense));

        // base * variación(0.85 a 1.0) * critico * efectividad
        damage *= 0.85 + random.nextDouble() * 0.15;
        damage *= isCritic ? CRIT_MULTIPLIER : 1.0;
        damage *= effectiveness;

        // Se redondea a entero minimo es 1
        return Math.max(1, (int) damage);
    }

    private double calcEffectiveness(String attackerType, String defenderType)
    {
        return EFFECTIVENESS.getOrDefault(attackerType + "-" + defenderType, 1.0);
    }
}