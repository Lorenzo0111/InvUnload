package de.jeff_media.InvUnload;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;

public class UnloadSummary {

    final HashMap<Location, EnumMap<Material, Integer>> unloads;

    UnloadSummary() {

        //System.out.println("Creating summary");
        unloads = new HashMap<>();
    }

    void protocolUnload(Location loc, Material mat, int amount) {
        if (amount == 0) return;
        if (!unloads.containsKey(loc)) {
            unloads.put(loc, new EnumMap<>(Material.class));
            unloads.get(loc).put(mat, amount);
        } else {
            if (unloads.get(loc).containsKey(mat)) {
                unloads.get(loc).put(mat, unloads.get(loc).get(mat) + amount);
            } else {
                unloads.get(loc).put(mat, amount);
            }
        }
    }

    String loc2str(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String name = loc.getBlock().getType().name();
        BlockState state = loc.getWorld().getBlockAt(x, y, z).getState();
        if (state instanceof Container) {
            Container container = (Container) state;
            if (container.getCustomName() != null) {
                name = container.getCustomName();
            }
        }
        return String.format(ChatColor.LIGHT_PURPLE + "§l%s   §r§a§lX: §f%d §a§lY: §f%d §a§lZ: §f%d", name, x, y, z);
    }

    String amount2str(int amount) {
        return String.format(ChatColor.DARK_PURPLE + "|§7%5dx  ", amount);
    }

    void print(PrintRecipient recipient, Player p) {
        if (unloads.size() > 0) printTo(recipient, p, Component.text(" "));
        for (Entry<Location, EnumMap<Material, Integer>> entry : unloads.entrySet()) {
            printTo(recipient, p, Component.text(" "));
            printTo(recipient, p, LegacyComponentSerializer.legacySection().deserialize(loc2str(entry.getKey())));
            EnumMap<Material, Integer> map = entry.getValue();
            for (Entry<Material, Integer> entry2 : map.entrySet()) {
                printTo(recipient, p,
                        LegacyComponentSerializer.legacySection()
                                .deserialize(amount2str(entry2.getValue()))
                                .append(
                                        Component.translatable(entry2.getKey().translationKey())
                                                .color(TextColor.color(255, 215, 0))
                                )
                );
            }
            //printTo(recipient,p," ");
        }
    }

    enum PrintRecipient {
        PLAYER, CONSOLE
    }

    void printTo(PrintRecipient recipient, Player p, Component text) {
        //System.out.println("Printing");
        if (recipient == PrintRecipient.CONSOLE) {
            Bukkit.getConsoleSender().sendMessage(text);
        } else {
            p.sendMessage(text);
        }
    }


}
