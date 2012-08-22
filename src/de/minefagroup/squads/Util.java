package de.minefagroup.squads;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Util {
	
	Squads master;
	
	public Util(Squads master){
		this.master = master;
	}
	
	public ChatColor colorByHealth(Player pl){
		ChatColor cc = ChatColor.DARK_PURPLE;
		if (pl.getHealth()>0){
			cc = ChatColor.RED;
		}
		if (pl.getHealth()>5){
			cc = ChatColor.YELLOW;
		}
		if (pl.getHealth()>15){
			cc = ChatColor.GREEN;
		}
		return cc;
	}
	
	public Player getPlayer(String plname) {
		return master.getServer().getPlayer(plname);
	}
	
	/*
	 * If you're having these methods in your plugin's main class (which extends
	 * JavaPlugin), you can remove parameters plugin from them, and in the
	 * FixedMetadataValue constructor and getMetadata method, use "this" instead
	 */
	public void setMetadata(Player player, String key, Object value,
			Squads plugin) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
	
	public String getMetadataString(Player player, String key, Squads plugin) {
		List<MetadataValue> values = player.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getDescription().getName()
					.equals(plugin.getDescription().getName())) {
				return value.asString();
			}
		}
		return null;
	}	
	
}
