package ru.func.raidarea.character;

import com.google.common.collect.Maps;

import java.util.Map;

public final class CharacterUtil {

    private static Map<String, ICharacter> characters = Maps.newHashMap();

    static Map<String, ICharacter> getCharacters() {
        return characters;
    }

    public static ICharacter getCharacterByName(String name) {
        return characters.get(name);
    }
}
