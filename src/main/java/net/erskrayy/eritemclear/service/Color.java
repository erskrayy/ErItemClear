package net.erskrayy.eritemclear.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Color {

    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.builder().character('&').hexColors().build();

    private Color() {}

    public static Component parse(String text) {
        return SERIALIZER.deserialize(text);
    }
}
