package com.rexcantor64.triton.wrappers.items;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.banners.Banner;
import com.rexcantor64.triton.banners.Colors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

public class ItemStackParser {

    private final static ItemFlag[] ITEM_FLAGS;

    static {
        // Enum value was renamed in MC 1.20.6
        ItemFlag hideAdditionalTooltipFlag = Stream.of("HIDE_POTION_EFFECTS", "HIDE_ADDITIONAL_TOOLTIP")
                .map(name -> {
                    try {
                        return ItemFlag.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to get HIDE_ADDITIONAL_TOOLTIP item flag"));

        ITEM_FLAGS = new ItemFlag[] {
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                hideAdditionalTooltipFlag,
        };
    }

    public static ItemStack bannerToItemStack(Banner banner, boolean active) {
        ItemStack is = new ItemStack(Triton.asSpigot().getWrapperManager().getBannerMaterial());
        BannerMeta bm = (BannerMeta) is.getItemMeta();
        for (Banner.Layer layer : banner.getLayers())
            bm.addPattern(new Pattern(getDyeColor(layer.getColor()), PatternType
                    .valueOf(layer.getPattern().getType())));
        bm.setDisplayName(ChatColor.translateAlternateColorCodes('&', banner.getDisplayName()));
        if (active)
            bm.setLore(Collections.singletonList(ChatColor
                    .translateAlternateColorCodes('&', Triton.get().getMessagesConfig().getMessage("other.selected"))));
        bm.addItemFlags(ITEM_FLAGS);
        is.setItemMeta(bm);
        return is;
    }

    private static DyeColor getDyeColor(Colors color) {
        if (color == Colors.GRAY && MinecraftVersion.AQUATIC_UPDATE.atOrAbove()) {
            // On 1.12 and below, this color is called "SILVER"
            return DyeColor.valueOf("SILVER");
        }
        return DyeColor.valueOf(color.getColor());
    }

}
