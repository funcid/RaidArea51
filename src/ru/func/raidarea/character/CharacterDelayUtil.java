package ru.func.raidarea.character;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

final class CharacterDelayUtil {

    private static final Map<UUID, Long> perkDelay = Maps.newHashMap();

    static boolean hasCountdown(final UUID user) {
        Long data = perkDelay.get(user);
        return data != null && data > System.currentTimeMillis();
    }

    static void setCountdown(final UUID user, final int val) {
        perkDelay.put(user, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(val));
    }

    static long getSecondsLeft(final UUID user) {
        return TimeUnit.MILLISECONDS.toSeconds(perkDelay.get(user) - System.currentTimeMillis());
    }
}
