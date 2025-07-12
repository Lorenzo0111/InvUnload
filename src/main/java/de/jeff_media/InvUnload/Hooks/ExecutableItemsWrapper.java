package de.jeff_media.InvUnload.Hooks;

import de.jeff_media.InvUnload.Main;
import org.bukkit.inventory.ItemStack;

public class ExecutableItemsWrapper {

    public static ExecutableItemsWrapper init(Main main) {

        if (!main.getConfig().getBoolean("use-executable-items")) return new ExecutableItemsWrapper();

        ExecutableItemsWrapper handler;
        try {
            handler = new ExecutableItemsHandler(main);
        } catch (final Throwable t) {

            handler = new ExecutableItemsWrapper();
        }
        return handler;
    }

    public String getExecutableItemsName(ItemStack item) {
        return null;
    }

    public boolean isExecutableItem(ItemStack item) {
        return false;
    }
}
