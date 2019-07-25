package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class ArnoldSchwarzenegger implements ICharacter {

    private final String     NAME = "Arnold Schwarzenegger";
    private final ItemStack  INFO = new ItemStack(Material.PAPER);
    private final ItemStack CLIPS = new ItemStack(Material.BLAZE_ROD, 12);
    private final Gun         GUN;

    public ArnoldSchwarzenegger() {
        ItemMeta itemMeta = INFO.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: §b§lАрнольд Шварценеггер");
        itemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж обладает невероятной силой,",
                "§fон способен поднять огромный камень и с легкостью",
                "§fшвырнуть в противника, так же его огромная мощь",
                "§fпозволяет стоят стрелять из пулемета, не звадую вашим врагам."
        ));
        INFO.setItemMeta(itemMeta);

        GUN = new GunBuilder()
                .material(Material.GOLD_AXE)
                .delay(12)
                .bullets(100)
                .damage(1.5)
                .clip(Material.BLAZE_ROD)
                .name("§e§lM134 Minigun §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fДа да, это тот самый пулемет, которым",
                        "§fон уничтожил огромное количество автомобилей в",
                        "«Терминатор 2: Судный день», это было нечто."

                ))
                .build();
    }

    @Override
    public void usePerk(final Player user) {
        if (CharacterDelayUtil.hasCountdown(user.getUniqueId()))
            return;
        FallingBlock fallingBlock = user.getWorld().spawnFallingBlock(user.getLocation().subtract(0, -1, 0), Material.IRON_BLOCK, (byte) 0);
        fallingBlock.setVelocity(user.getEyeLocation().getDirection().multiply(2));
        CharacterDelayUtil.setCountdown(user.getUniqueId(), 8);
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
    }

    @Override
    public Gun getGunWeapon() {
        return GUN;
    }
}
