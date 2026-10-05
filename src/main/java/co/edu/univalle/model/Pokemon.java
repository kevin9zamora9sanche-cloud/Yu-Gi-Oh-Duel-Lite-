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

    public Pokemon(String name, int hp, int attack, int defense, int speed, String primaryType, String spriteUrl) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.primaryType = primaryType;
        this.spriteUrl = spriteUrl;
    }

    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = Math.max(0, currentHp); }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public String getPrimaryType() { return primaryType; }
    public String getSpriteUrl() { return spriteUrl; }

    public boolean isFainted() {
        return this.currentHp <= 0;
    }
}