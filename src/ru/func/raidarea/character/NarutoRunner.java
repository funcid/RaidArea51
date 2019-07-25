package ru.func.raidarea.character;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.Arrays;

public class NarutoRunner implements ICharacter {

    private final String        NAME = "§e§lНаруто Фан";
    private final ItemStack     INFO = new ItemStack(Material.PAPER);
    private final ItemStack    CLIPS = new ItemStack(Material.FIREWORK_CHARGE, 16);
    private final Gun            GUN;
    private final PotionEffect SPEED;

    public NarutoRunner() {
        ItemMeta itemMeta = INFO.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: " + NAME);
        itemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж обладает огромной скоростью,",
                "§fкогда заносит руки за спину и начинает бежать",
                "§fон становится настолько быстрым, что даже пули",
                "§fне способны его остановить.",
                "",
                "§7[§fНе повторять в реальной жизни, вы упадете лицом в пол§7]"
        ));
        INFO.setItemMeta(itemMeta);

        SPEED = new PotionEffect(PotionEffectType.SPEED, 99999, 25);

        GUN = new GunBuilder()
                .material(Material.WOOD_AXE)
                .delay(1)
                .bullets(1)
                .damage(6)
                .clip(Material.FIREWORK_CHARGE)
                .name("§e§lРогатка §b[ §f§l%d §b]")
                .lore(Arrays.asList(
                        "",
                        "§fВы нашли ее на чердаке когда готовились к осаде,",
                        "§fее там оставил ваш дедушка, когда вам было 5 лет,",
                        "§fвы очень любили из нее стрелять по банкам,",
                        "§fи я хочу сказать, не зря, у вас есть огромный талант,",
                        "§fкоторым вы можете воспользоваться на рейде."
                ))
                .build();
    }

    @Override
    public void usePerk(final Player user) {
        if (user.isSneaking()) user.removePotionEffect(PotionEffectType.SPEED);
        else user.addPotionEffect(SPEED);
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

