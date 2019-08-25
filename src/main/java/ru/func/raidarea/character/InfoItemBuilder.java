package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InfoItemBuilder {

    private String name;
    private final List<String> lore = new ArrayList<>();

    public InfoItemBuilder name(final String name) {
        this.name = name;
        return this;
    }
    public InfoItemBuilder lore(final String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemStack build() {

        final ItemStack infoItem = new ItemStack(Material.PAPER);
        final ItemMeta meta = infoItem.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        infoItem.setItemMeta(meta);
        return infoItem;
    }
}
