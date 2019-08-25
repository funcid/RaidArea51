package ru.func.raidarea.character.attack;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.func.raidarea.character.CharacterDelayUtil;
import ru.func.raidarea.character.InfoItemBuilder;
import ru.func.raidarea.weapon.Weaponry;
import ru.func.raidarea.weapon.gun.GunBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class ElonMusk implements Attacker {

    @Getter
    private final Weaponry weapon;
    @Getter
    private final String name = "§e§lИлон Маск";

    private final ItemStack info;
    private final ItemStack clips = new ItemStack(Material.DIAMOND, 8);

    public ElonMusk() {

        info = new InfoItemBuilder()
                .name("§fИнформация о персонаже: " + name)
                .lore("")
                .lore("§fВаш персонаж обладает огромными средтвами и умом,")
                .lore("§fв свое лучше время он основал SpaceX и Tesla")
                .lore("§fпо-этому создать космический луч со спутника")
                .lore("§fдля него не составит больших хлапот.")
                .build();

        weapon = new GunBuilder()
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
        currentPlayer.getInventory().setItem(0, weapon.getItemStack());
        currentPlayer.getInventory().setItem(1, clips);
        currentPlayer.getInventory().setItem(8, info);
    }
}
