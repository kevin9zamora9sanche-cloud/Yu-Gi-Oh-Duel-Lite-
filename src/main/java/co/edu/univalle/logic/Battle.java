package co.edu.univalle.logic;

import co.edu.univalle.model.Pokemon;
import java.util.Random;

public class Battle {
    private final Pokemon p1;
    private final Pokemon p2;
    private final BattleListener listener;
    private final Random random = new Random();

    public Battle(Pokemon p1, Pokemon p2, BattleListener listener) {
        this.p1 = p1;
        this.p2 = p2;
        this.listener = listener;
    }

    public void startBattle() {
        // Se ejecuta en un hilo secundario para evitar bloquear el hilo de Swing (UI)
        new Thread(() -> {
            Pokemon attacker = p1;
            Pokemon defender = p2;

            // Determinar orden de turnos por Speed (empate aleatorio)
            if (p2.getSpeed() > p1.getSpeed()) {
                attacker = p2;
                defender = p1;
            } else if (p1.getSpeed() == p2.getSpeed()) {
                if (random.nextBoolean()) {
                    attacker = p2;
                    defender = p1;
                }
            }

            // Bucle del combate por turnos
            while (!p1.isFainted() && !p2.isFainted()) {
                try {
                    Thread.sleep(1200); // Pausa visual entre ataques
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                executeTurn(attacker, defender);

                if (defender.isFainted()) {
                    if (listener != null) {
                        listener.onBattleEnded(attacker.getName());
                    }
                    break;
                }

                // Alternar turnos
                Pokemon temp = attacker;
                attacker = defender;
                defender = temp;
            }
        }).start();
    }

    private void executeTurn(Pokemon attacker, Pokemon defender) {
        // 1. Probabilidad de golpe crítico (10% de probabilidad -> x1.5)
        boolean isCritical = random.nextDouble() < 0.10;
        double critMult = isCritical ? 1.5 : 1.0;

        // 2. Modificador de efectividad por tipo primario (Agua, Fuego, Planta)
        double typeModifier = getTypeEffectiveness(attacker.getPrimaryType(), defender.getPrimaryType());

        // 3. Cálculo de daño base segun la formula elegida: (ATK * rand - DEF * rand)
        double randomAtk = random.nextDouble();
        double randomDef = random.nextDouble();

        double rawDamage = (attacker.getAttack() * randomAtk) - (defender.getDefense() * randomDef);
        if (rawDamage < 5) {
            rawDamage = 5; // Daño minimo para asegurar progreso
        }

        int finalDamage = (int) Math.round(rawDamage * critMult * typeModifier);

        // Aplicar daño al defensor
        defender.applyDamage(finalDamage);

        // Notificar los eventos obligatorios al listener
        if (listener != null) {
            listener.onTurn(attacker.getName(), defender.getName(), finalDamage, isCritical, typeModifier);
            listener.onHpChanged(defender.getName(), defender.getCurrentHp());
        }
    }

    private double getTypeEffectiveness(String typeAttacker, String typeDefender) {
        String atk = typeAttacker.toLowerCase();
        String def = typeDefender.toLowerCase();

        // Ventajas (x1.3)
        if (atk.equals("water") && def.equals("fire")) return 1.3;
        if (atk.equals("fire") && def.equals("grass")) return 1.3;
        if (atk.equals("grass") && def.equals("water")) return 1.3;

        // Desventajas (x0.7)
        if (atk.equals("fire") && def.equals("water")) return 0.7;
        if (atk.equals("grass") && def.equals("fire")) return 0.7;
        if (atk.equals("water") && def.equals("grass")) return 0.7;

        return 1.0; // Resto de combinaciones
    }
}