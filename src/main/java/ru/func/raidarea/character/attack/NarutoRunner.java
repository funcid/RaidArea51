package ru.func.raidarea.character.attack;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.raidarea.character.InfoItemBuilder;
import ru.func.raidarea.weapon.gun.GunBuilder;
import ru.func.raidarea.weapon.gun.Shooting;

import java.util.ArrayList;
import java.util.Arrays;

public class NarutoRunner implements Attacker {

    @Getter
    private final String name = "§e§lНаруто Раннер";

    private final ItemStack info;
    private final ItemStack clips = new ItemStack(Material.FIREWORK_CHARGE, 16);

    @Getter
    private final Shooting weapon;
    private final PotionEffect speed;

    public NarutoRunner() {

        info = new InfoItemBuilder()
                .name("§fИнформация о персонаже: " + name)
                .lore("")
                .lore("§fВаш персонаж обладает огромной скоростью,")
                .lore("§fкогда заносит руки за спину и начинает бежать")
                .lore("§fон становится настолько быстрым, что даже пули")
                .lore("§fне способны его остановить.")
                .build();

        speed = new PotionEffect(PotionEffectType.SPEED, 99999, 20);

        weapon = new GunBuilder()
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
        currentPlayer.getInventory().setItem(0, weapon.getItemStack());
        currentPlayer.getInventory().setItem(1, clips);
        currentPlayer.getInventory().setItem(8, info);
    }
}

