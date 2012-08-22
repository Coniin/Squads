package de.minefagroup.squads.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import de.minefagroup.squads.Squads;
import de.minefagroup.squads.customLists.Graveyard;
import de.minefagroup.squads.customLists.Party;

public class TargetListener implements Listener {
	
	Squads master;
	Graveyard gy;
	
	public TargetListener(Squads plugin){
		this.master = plugin;
		master.getServer().getPluginManager().registerEvents(this, plugin);
		gy = master.getGraveyard();
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent ete){
		//Cancel targeting of DeadPlayers
		if (ete.getTarget() instanceof Player){
			Player pl = (Player) ete.getTarget();
			Party plParty = master.getPartyManager().getParty(pl.getName());
			if (plParty!=null){
				for (String corpseNames : gy.getCorpses()){
					if (corpseNames.equalsIgnoreCase(pl.getName())){
						ete.setCancelled(true);
						ete.setTarget(null);
					}
				}
			}
		}
	}
	
}
