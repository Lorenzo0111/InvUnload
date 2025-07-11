package de.jeff_media.InvUnload.Hooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.jeff_media.InvUnload.Main;
import org.bukkit.plugin.Plugin;

public class PlotSquaredHook {
	
	final Main main;
	PlotSquaredUniversalHook hook;

	public PlotSquaredHook(Main main) {
		this.main=main;

		try {
			Class<?> bukkitUtilClass = Class.forName("com.plotsquared.bukkit.util.BukkitUtil");
			bukkitUtilClass.getMethod("adapt", Location.class);
			hook = new PlotSquared6Hook();
			main.getLogger().info("Successfully hooked into PlotSquared v6");
			return;
		} catch(Exception ignored) {

		}
		Plugin plotSquared = Bukkit.getPluginManager().getPlugin("PlotSquared");
		if(plotSquared != null) {
			main.getLogger().warning("Could not hook into PlotSquared although it is installed (version " + plotSquared.getDescription().getVersion()+")");
			hook = null;
		}
		
	}
	
	public boolean isBlockedByPlotSquared(Block block, Player player) {
		
		if(hook==null) return false;
		
		if(!main.getConfig().getBoolean("use-plotsquared")) return false;
		
		if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
			//System.out.println("PlotSquared not installed");
			return false;
		}
		
		return hook.isBlockedByPlotSquared(block, player, main);
		
	}
}
