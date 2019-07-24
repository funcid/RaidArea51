package ru.func.raidarea.character;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.raidarea.weapon.Gun;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ElonMusk implements ICharacter {

    private final String NAME = "Elon Musk";
    private final ItemStack INFO = new ItemStack(Material.PAPER);

    private final Map<UUID, Long> perkDelay = Maps.newHashMap();

    private boolean hasCountdown(UUID user) {
        Long data = perkDelay.get(user);
        return data != null && data > System.currentTimeMillis();
    }

    private void setCountdown(UUID user, int val, TimeUnit unit) {
        perkDelay.put(user, System.currentTimeMillis() + unit.toMillis(val));
    }

    public ElonMusk() {
        CharacterUtil.getCharacters().put(NAME, this);

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
    }

    @Override
    public void usePerk(Player user) {
        if (hasCountdown(user.getUniqueId()))
            return;
        for (int i = 0; i< 4; i++)
            user.getWorld().strikeLightningEffect(user.getTargetBlock(null, 500).getLocation());
        setCountdown(user.getUniqueId(), 12, TimeUnit.SECONDS);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void giveAmmunition(Player currentPlayer) {
        currentPlayer.getInventory().setItem(8, INFO);
    }

    @Override
    public Gun getGunWeapon() {
        return null;
    }
}
