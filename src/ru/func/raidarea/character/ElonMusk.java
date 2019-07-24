package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class ElonMusk implements ICharacter {

    private final String     NAME = "Elon Musk";
    private final ItemStack  INFO = new ItemStack(Material.PAPER);
    private final ItemStack CLIPS = new ItemStack(Material.ARROW, 8);
    private final Gun         GUN;

    public ElonMusk() {
        ItemMeta itemMeta = INFO.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: §b§lИлон Маск");
        itemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж обладает огромными средтвами и умом,",
                "§fв свое лучше время он основал SpaceX и Tesla",
                "§fпо-этому создать космический луч со спутника",
                "§fдля него не составит больших хлапот."
        ));
        INFO.setItemMeta(itemMeta);

        GUN = new GunBuilder()
                .material(Material.GOLD_HOE)
                .delay(5)
                .bullets(5)
                .damage(8)
                .clip(Material.ARROW)
                .name("§e§lАвтоЭлектроСнайперка §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fОчень мощная штука...",
                        "§fБольше толко и сказать не чего."

                ))
                .build();
    }

    @Override
    public void usePerk(final Player user) {
        if (CharacterDelayUtil.hasCountdown(user.getUniqueId()))
            return;
        for (int i = 0; i< 4; i++)
            user.getWorld().strikeLightningEffect(user.getTargetBlock(null, 500).getLocation());
        CharacterDelayUtil.setCountdown(user.getUniqueId(), 12);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, GUN.getItemStack());
        currentPlayer.getInventory().setItem(2, CLIPS);
        currentPlayer.getInventory().setItem(8, INFO);
    }

    @Override
    public Gun getGunWeapon() {
        return GUN;
    }
}
