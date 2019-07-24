package ru.func.raidarea.weapon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GunBuilder {

    private ItemStack itemStack;
    private Material material;
    private String name;
    private int bullets;
    private int delay;
    private Material clipMaterial;
    private double damage;

    public GunBuilder material(Material material) {
        this.material = material;
        this.itemStack = new ItemStack(material);
        return this;
    }

    public GunBuilder name(String name) {
        this.name = name;
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(String.format(name, bullets));
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public GunBuilder lore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public GunBuilder bullets(int bullets) {
        itemStack.setDurability((short) bullets);
        this.bullets = bullets;
        return this;
    }

    public GunBuilder delay(int delay) {
        this.delay = delay;
        return this;
    }

    public GunBuilder clip(Material clipMaterial) {
        this.clipMaterial = clipMaterial;
        return this;
    }

    public GunBuilder damage(double damage) {
        this.damage = damage;
        return this;
    }

    public Gun build() {
        return new Gun(this);
    }

    ItemStack getItemStack() {
        return itemStack;
    }

    Material getMaterial() {
        return material;
    }

    String getName() {
        return name;
    }

    int getBullets() {
        return bullets;
    }

    int getDelay() {
        return delay;
    }

    Material getClipMaterial() {
        return clipMaterial;
    }

    double getDamage() {
        return damage;
    }
}
