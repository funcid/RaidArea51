package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class Soldier implements ICharacter {

    private final String          NAME = "§lСолдат Армии США";
    private final ItemStack       INFO = new ItemStack(Material.PAPER);
    private final ItemStack      CLIPS = new ItemStack(Material.GLOWSTONE_DUST, 14);
    private final Gun              GUN;

    private final ItemStack     HELMET = new ItemStack(Material.LEATHER_HELMET);
    private final ItemStack CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
    private final ItemStack    LEGGINS = new ItemStack(Material.LEATHER_LEGGINGS);
    private final ItemStack      BOOTS = new ItemStack(Material.LEATHER_BOOTS);


    public Soldier() {
        ItemMeta infoItemMeta = INFO.getItemMeta();
        infoItemMeta.setDisplayName("§fИнформация о персонаже: " + NAME);
        infoItemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж это рядовой солдат,",
                "§fкоторому надо любой ценой укрыть секреты",
                "§fзоны 51, да бы всякие невежды не знали о",
                "§fсуществавании иных форм жизни."
        ));
        INFO.setItemMeta(infoItemMeta);

        setUnbreakable(HELMET);
        setUnbreakable(CHESTPLATE);
        setUnbreakable(LEGGINS);
        setUnbreakable(BOOTS);

        GUN = new GunBuilder()
                .material(Material.DIAMOND_PICKAXE)
                .delay(5)
                .bullets(25)
                .damage(4.5)
                .clip(Material.GLOWSTONE_DUST)
                .name("§e§lM4A1-S Carbine §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fНевероятно удобная и в тоже время,",
                        "§fлегко контролируемая автоматическая винтовка",
                        "§fспособная запросто убить любого осмелевшего",
                        "§fпосигнуть на секреты зоны 51."
                ))
                .build();

        ItemMeta clipItemMeta = CLIPS.getItemMeta();
        infoItemMeta.setDisplayName("§f§lОбойма");
        CLIPS.setItemMeta(clipItemMeta);
    }

    @Override
    public void usePerk(final Player user) {

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, GUN.getItemStack());
        currentPlayer.getInventory().setItem(1, CLIPS);
        currentPlayer.getInventory().setItem(8, INFO);

        currentPlayer.getInventory().setHelmet(HELMET);
        currentPlayer.getInventory().setChestplate(CHESTPLATE);
        currentPlayer.getInventory().setLeggings(LEGGINS);
        currentPlayer.getInventory().setBoots(BOOTS);
    }

    @Override
    public Gun getGunWeapon() {
        return GUN;
    }

    private void setUnbreakable(final ItemStack currentItem) {
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.setUnbreakable(true);
        currentItem.setItemMeta(itemMeta);
    }
}
