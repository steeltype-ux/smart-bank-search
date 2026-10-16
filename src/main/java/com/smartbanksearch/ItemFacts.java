package com.smartbanksearch;

/**
 * Normalized item metadata. Slot and combat stats are supplied by RuneLite when available.
 * Unknown metadata uses slot=-1 and equipable=false; name-only rules remain available.
 * Pure Java class so matching rules can be tested without a running game client.
 */
public final class ItemFacts
{
    public final String name;
    public final int slot;
    public final boolean equipable;
    public final boolean twoHanded;
    public final int meleeAttack;
    public final int rangedAttack;
    public final int magicAttack;
    public final int meleeStrength;
    public final int rangedStrength;
    public final float magicDamage;
    public final int prayerBonus;
    public final int defence;

    public ItemFacts(String name, int slot, boolean equipable, boolean twoHanded,
                     int meleeAttack, int rangedAttack, int magicAttack, int meleeStrength,
                     int rangedStrength, float magicDamage, int prayerBonus, int defence)
    {
        this.name = name == null ? "" : name;
        this.slot = slot;
        this.equipable = equipable;
        this.twoHanded = twoHanded;
        this.meleeAttack = meleeAttack;
        this.rangedAttack = rangedAttack;
        this.magicAttack = magicAttack;
        this.meleeStrength = meleeStrength;
        this.rangedStrength = rangedStrength;
        this.magicDamage = magicDamage;
        this.prayerBonus = prayerBonus;
        this.defence = defence;
    }

    public static ItemFacts nameOnly(String name)
    {
        return new ItemFacts(name, -1, false, false, 0, 0, 0, 0, 0, 0f, 0, 0);
    }

    public boolean worn() { return equipable || slot >= 0; }
}
