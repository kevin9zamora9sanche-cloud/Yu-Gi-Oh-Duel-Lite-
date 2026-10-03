package co.edu.univalle.model;

public class Pokemon {
    private String name;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;
    private String primaryType;
    private String spriteUrl;

    public Pokemon(String name, int maxHp, int attack, int defense, int speed, String primaryType, String spriteUrl) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp; // Inicialmente la vida actual es igual al HP máximo
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.primaryType = primaryType;
        this.spriteUrl = spriteUrl;
    }

    // Métodos de lógica del modelo
    public boolean isFainted() {
        return this.currentHp <= 0;
    }

    public void applyDamage(int damage) {
        this.currentHp -= damage;
        if (this.currentHp < 0) {
            this.currentHp = 0; // Regla del laboratorio: El HP no puede ser negativo
        }
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }
}