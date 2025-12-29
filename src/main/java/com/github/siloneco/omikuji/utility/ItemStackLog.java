package com.github.siloneco.omikuji.utility;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemStackLog {
    private ItemStackLog() {}

    public static String summarize(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return "[]";
        }

        return items.stream()
                .filter(Objects::nonNull)
                .filter(it -> it.getType() != Material.AIR)
                .map(it -> it.getType().name() + "x" + it.getAmount())
                .collect(Collectors.joining(", ", "[", "]"));
    }
}


