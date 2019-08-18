package ru.func.raidarea.character;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.weapon.Gun;
import ru.func.raidarea.weapon.GunBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class NarutoRunner implements Characterful {

    @Getter
    private final String name = "§e§lНаруто Раннер";

    private final ItemStack info = new ItemStack(Material.PAPER);
    private final ItemStack clips = new ItemStack(Material.FIREWORK_CHARGE, 16);

    @Getter
    private final Gun gunWeapon;
    private final PotionEffect speed;

    public NarutoRunner() {
        ItemMeta itemMeta = info.getItemMeta();
        itemMeta.setDisplayName("§fИнформация о персонаже: " + name);
        itemMeta.setLore(Arrays.asList(
                "",
                "§fВаш персонаж обладает огромной скоростью,",
                "§fкогда заносит руки за спину и начинает бежать",
                "§fон становится настолько быстрым, что даже пули",
                "§fне способны его остановить.",
                "",
                "§7[§fНе повторять в реальной жизни, вы упадете лицом в пол§7]"
        ));
        info.setItemMeta(itemMeta);

        speed = new PotionEffect(PotionEffectType.SPEED, 99999, 20);

        gunWeapon = new GunBuilder()
                .material(Material.WOOD_AXE)
                .delay(1)
                .bullets(1)
                .damage(6)
                .clip(Material.FIREWORK_CHARGE)
                .name("§e§lРогатка §b[ §f§l%d §b]")
                .lore(new ArrayList<>(Arrays.asList(
                        "",
                        "§fВы нашли ее на чердаке когда готовились к осаде,",
                        "§fее там оставил ваш дедушка, когда вам было 5 лет,",
                        "§fвы очень любили из нее стрелять по банкам,",
                        "§fи я хочу сказать, не зря, у вас есть огромный талант,",
                        "§fкоторым вы можете воспользоваться на рейде."
                )))
                .build();
    }

    @Override
    public void usePerk(final Player user) {
        if (user.isSneaking()) user.removePotionEffect(PotionEffectType.SPEED);
        else user.addPotionEffect(speed);
    }

    @Override
    public void giveAmmunition(final Player currentPlayer) {
        currentPlayer.getInventory().setItem(0, gunWeapon.getItemStack());
        currentPlayer.getInventory().setItem(1, clips);
        currentPlayer.getInventory().setItem(8, info);
    }
}

