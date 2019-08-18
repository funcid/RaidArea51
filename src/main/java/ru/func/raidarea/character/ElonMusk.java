package ru.func.raidarea.character;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class ElonMusk implements ICharacter {

    @Getter
    private final Gun gunWeapon;
    @Getter
    private final String name = "§e§lИлон Маск";

    private final ItemStack info = new ItemStack(Material.PAPER);
    private final ItemStack clips = new ItemStack(Material.DIAMOND, 8);

    public ElonMusk() {
        ItemMeta itemMeta = info.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: " + name);
        itemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж обладает огромными средтвами и умом,",
                "§fв свое лучше время он основал SpaceX и Tesla",
                "§fпо-этому создать космический луч со спутника",
                "§fдля него не составит больших хлапот."
        ));
        info.setItemMeta(itemMeta);

        gunWeapon = new GunBuilder()
                .material(Material.GOLD_HOE)
                .delay(5)
                .bullets(5)
                .damage(6.5)
                .clip(Material.DIAMOND)
                .name("§e§lАвтоЭлектроСнайперка §b[ §f§l%d §b]")
                .lore(new ArrayList<>(Arrays.asList(
                        "",
                        "§fОчень мощная штука...",
                        "§fБольше толко и сказать не чего."

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
        for (int i = 0; i < 4; i++)
            user.getWorld().strikeLightning(user.getTargetBlock(null, 50).getLocation());
        CharacterDelayUtil.setCountdown(user.getUniqueId(), 12);
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, gunWeapon.getItemStack());
        currentPlayer.getInventory().setItem(1, clips);
        currentPlayer.getInventory().setItem(8, info);
    }
}
