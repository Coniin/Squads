package de.minefagroup.squads.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.minefagroup.squads.Squads;

public class LogoutListener implements Listener{
	
	Squads master;
	
	public LogoutListener(Squads plugin){
		master = plugin;
		master.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuitEvent(Player pl, String quitMsg){
		if (master.getPartyManager().getPlayersParty(pl)!=null){
			//Need workover
			master.getPartyManager().leaveParty(pl);
		}
	}
	
}
