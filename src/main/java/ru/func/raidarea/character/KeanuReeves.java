package ru.func.raidarea.character;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class KeanuReeves implements ICharacter {

    @Getter
    private final String name = "§e§lКеану Ривз";
    private final ItemStack INFO = new ItemStack(Material.PAPER);
    private final ItemStack HOOK = new ItemStack(Material.FISHING_ROD);
    private final ItemStack CLIPS = new ItemStack(Material.CLAY_BRICK, 16);
    @Getter
    private final Gun gunWeapon;

    public KeanuReeves() {
        ItemMeta itemMeta = INFO.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: " + name);
        itemMeta.setLore(Arrays.asList(
                "",
                "§fБог киберпанка."
        ));
        INFO.setItemMeta(itemMeta);

        itemMeta = HOOK.getItemMeta();
        itemMeta.setDisplayName("§e§lХук");
        itemMeta.setUnbreakable(true);
        HOOK.setItemMeta(itemMeta);

        gunWeapon = new GunBuilder()
                .material(Material.WOOD_HOE)
                .delay(4)
                .bullets(10)
                .damage(6)
                .clip(Material.CLAY_BRICK)
                .name("§e§lUSP-S §b[ §f§l%d §b]")
                .lore(new ArrayList<>(Arrays.asList(
                        "",
                        "§fОбычное, но не менее опасное оружие."
                )))
                .build();
    }

    @Override
    public void usePerk(final Player user) {
        if (user.isSneaking())
            return;
        if (CharacterDelayUtil.hasCountdown(user.getUniqueId())) {
            user.sendMessage("[§b!§f] §7Подождите еще §f§l" + (CharacterDelayUtil.getSecondsLeft(user.getUniqueId()) + 1) + "§7 секунд(ы).");
            return;
        }
        if (user.getPassengers().size() > 0)
            return;
        user.setVelocity(user.getEyeLocation().getDirection().multiply(0.01).subtract(new Vector(0, -1.3, 0)));
        CharacterDelayUtil.setCountdown(user.getUniqueId(), 8);
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, gunWeapon.getItemStack());
        currentPlayer.getInventory().setItem(1, HOOK);
        currentPlayer.getInventory().setItem(2, CLIPS);
        currentPlayer.getInventory().setItem(8, INFO);
    }
}
