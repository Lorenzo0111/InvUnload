package de.jeff_media.InvUnload.Hooks;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import de.jeff_media.InvUnload.Main;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public final class ExecutableItemsHandler extends ExecutableItemsWrapper {

    private boolean installed;

    public ExecutableItemsHandler(final Main main) {

        if (Bukkit.getPluginManager().getPlugin("ExecutableItems") == null) {
            installed = false;
            return;
        }

        try {
            Class.forName("com.ssomar.score.api.executableitems.ExecutableItemsAPI");
            installed = true;
        } catch (Throwable t) {
            main.getLogger().warning("Found ExecutableItems plugin but could not hook into it.");
            t.printStackTrace();
            installed = false;
        }
    }

    @Override
    public String getExecutableItemsName(ItemStack item) {
        if(!installed) return null;
        return ExecutableItemsAPI.getExecutableItemsManager()
                .getExecutableItem(item)
                .map(ExecutableItemInterface::getId)
                .orElse(null);
    }

    @Override
    public boolean isExecutableItem(ItemStack item) {
        return installed && ExecutableItemsAPI.getExecutableItemsManager()
                .getExecutableItem(item).isPresent();
    }

}
