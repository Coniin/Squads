package de.minefagroup.squads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import de.minefagroup.commands.PartyCmdExecutor;
import de.minefagroup.squads.Listener.DamageListener;
import de.minefagroup.squads.Listener.LogoutListener;
import de.minefagroup.squads.Listener.TargetListener;

public class Squads extends JavaPlugin {

	ArrayList<Party> openPartys;
	public PartyCmdExecutor pce = null;

	public void onEnable() {
		openPartys = new ArrayList<Party>();
		new DamageListener(this);
		new LogoutListener(this);
		new TargetListener(this);
		pce = new PartyCmdExecutor(this);
		getCommand("party").setExecutor(pce);
	}

	public void onDisable() {
		openPartys = null; // Safe
		for (Player pl: this.getServer().getOnlinePlayers()){
			//Need to Find workaround
			pce.leaveParty(pl);
		}
	}

//--------------Util:
//-------------
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
	
	public void registerParty(Party toReg) {
		openPartys.add(toReg);
	}

	public void unregisterParty(Party toReg) {
		openPartys.remove(toReg);
	}

	public Player getPlayer(String plname) {
		return this.getServer().getPlayer(plname);
	}
	
	public Party getPlayersParty(Player pl) {
		String partyName = this.getMetadataString(pl, "Party", this);
		return getParty(partyName);
	}
	
	public Party getParty(String pName) {
		if (openPartys!=null){
			for (Party parties : openPartys) {
				if (parties.getName().equalsIgnoreCase(pName)) {
					return parties;
				}
			}
		}	
		return null;
	}
	
	public ArrayList<Party> getOpenPartys(){
		return openPartys;
	}
	
	public boolean partyExists(String partyN){
		if (openPartys!=null){
			for (Party parties: openPartys){
				if (parties.getName().equalsIgnoreCase(partyN)){
					return true;
				}
			}
		}	
		return false;
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
	
	
	
	

