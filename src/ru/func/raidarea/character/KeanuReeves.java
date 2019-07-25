package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class KeanuReeves implements ICharacter {

    private final String     NAME = "Keanu Reeves";
    private final ItemStack  INFO = new ItemStack(Material.PAPER);
    private final ItemStack  HOOK = new ItemStack(Material.FISHING_ROD);
    private final ItemStack CLIPS = new ItemStack(Material.CLAY_BRICK, 16);
    private final Gun         GUN;

    public KeanuReeves() {
        ItemMeta itemMeta = INFO.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: §b§lКиано Ривз");
        itemMeta.setLore(Arrays.asList(
                "",
                "§fБог киберпанка."
        ));
        INFO.setItemMeta(itemMeta);

        itemMeta = HOOK.getItemMeta();
        itemMeta.setDisplayName("§e§lХук с левой, хус с правой");
        itemMeta.setUnbreakable(true);
        HOOK.setItemMeta(itemMeta);

        GUN = new GunBuilder()
                .material(Material.WOOD_HOE)
                .delay(4)
                .bullets(10)
                .damage(4)
                .clip(Material.CLAY_BRICK)
                .name("§e§lUSP-S §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fОбычное, но не менее опасное оружие."
                ))
                .build();
    }

    @Override
    public void usePerk(final Player user) { }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, GUN.getItemStack());
        currentPlayer.getInventory().setItem(1, HOOK);
        currentPlayer.getInventory().setItem(2, CLIPS);
        currentPlayer.getInventory().setItem(8, INFO);
    }

    @Override
    public Gun getGunWeapon() {
        return GUN;
    }
}
