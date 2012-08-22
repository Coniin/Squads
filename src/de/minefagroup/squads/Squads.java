package de.minefagroup.squads;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.minefagroup.commands.PartyCmdExecutor;
import de.minefagroup.squads.Listener.DamageListener;
import de.minefagroup.squads.Listener.LogoutListener;
import de.minefagroup.squads.Listener.TargetListener;
import de.minefagroup.squads.customLists.Graveyard;

public class Squads extends JavaPlugin {

	private PartyManager pm = null;
	private Util util = null;
	private Graveyard gy = null;

	public void onEnable() {
		pm = new PartyManager(this);
		util = new Util(this);
		gy = new Graveyard();
		
		new DamageListener(this);
		new LogoutListener(this);
		new TargetListener(this);
		getCommand("party").setExecutor( new PartyCmdExecutor(this));
	}

	public void onDisable() {
		for (Player pl: this.getServer().getOnlinePlayers()){
			//safe and reload
			pm.leaveParty(pl);
		}
		pm = null;
		util = null;
	}
	
	public PartyManager getPartyManager(){
		return pm;
	}
	
	public Util getUtil(){
		return util;
	}
	
	public Graveyard getGraveyard(){
		return gy;
	}
	
}
	
	
	
	

