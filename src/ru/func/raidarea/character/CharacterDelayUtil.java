package ru.func.raidarea.character;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CharacterDelayUtil {

    private static final Map<UUID, Long> perkDelay = Maps.newHashMap();

    public static boolean hasCountdown(UUID user) {
        Long data = perkDelay.get(user);
        return data != null && data > System.currentTimeMillis();
    }

    public static void setCountdown(UUID user, int val, TimeUnit unit) {
        perkDelay.put(user, System.currentTimeMillis() + unit.toMillis(val));
    }
}
