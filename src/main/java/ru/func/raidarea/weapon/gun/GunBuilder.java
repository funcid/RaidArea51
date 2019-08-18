package ru.func.raidarea.weapon.gun;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public class GunBuilder {

    private ItemStack itemStack;
    private Material material;
    private String name;
    private int bullets;
    private int delay;
    private Material clipMaterial;
    private double damage;

    public GunBuilder material(final Material material) {
        this.material = material;
        this.itemStack = new ItemStack(material);
        return this;
    }

    public GunBuilder name(final String name) {
        this.name = name;
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(String.format(name, bullets));
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public GunBuilder lore(final List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        lore.add("");
        lore.add("§fУрон: §c§l" + damage);
        lore.add("§fМаксимум выстрелов: §e§l" + bullets);
        lore.add("§fВремя перезардки (с): §b§l" + delay);
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public GunBuilder bullets(final int bullets) {
        itemStack.setDurability((short) bullets);
        this.bullets = bullets;
        return this;
    }

    public GunBuilder delay(final int delay) {
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
}
