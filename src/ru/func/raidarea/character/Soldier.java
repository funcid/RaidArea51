package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class Soldier implements ICharacter {

    private final String NAME = "American Soldier";
    private final Gun GUN;
    private final ItemStack INFO = new ItemStack(Material.PAPER);
    private final ItemStack CLIP = new ItemStack(Material.FIREWORK_CHARGE, 4);


    public Soldier() {
        CharacterUtil.getCharacters().put(NAME, this);

        ItemMeta infoItemMeta = INFO.getItemMeta();
        infoItemMeta.setDisplayName("§fИнформация о персонаже: §b§lСолдат Армии США");
        infoItemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж это рядовой солдат,",
                "§fкоторому надо любой ценой укрыть секреты",
                "§fзоны 51, да бы всякие невежды не знали о",
                "§fсуществавании иных форм жизни."
        ));
        INFO.setItemMeta(infoItemMeta);

        GUN = new GunBuilder()
                .material(Material.DIAMOND_PICKAXE)
                .delay(5)
                .bullets(25)
                .damage(7)
                .clip(Material.FIREWORK_CHARGE)
                .name("§e§lM4A1-S Carbine §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fНевероятно удобная и в тоже время,",
                        "§fлегко контролируемая автоматическая винтовка",
                        "§fспособная запросто убить любого осмелевшего",
                        "§fпосигнуть на секреты зоны 51."
                ))
                .build();

        ItemMeta clipItemMeta = CLIP.getItemMeta();
        infoItemMeta.setDisplayName("§f§lОбойма");
        CLIP.setItemMeta(clipItemMeta);
    }

    @Override
    public void usePerk(Player user) {

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void giveAmmunition(Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, GUN.getItemStack());
        currentPlayer.getInventory().setItem(1, CLIP);
        currentPlayer.getInventory().setItem(8, INFO);
    }

    @Override
    public Gun getGunWeapon() {
        return GUN;
    }
}
