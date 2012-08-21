package de.minefagroup.squads.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import de.minefagroup.squads.Party;
import de.minefagroup.squads.Squads;

public class TargetListener implements Listener {
	
	Squads master;
	
	public TargetListener(Squads plugin){
		this.master = plugin;
		master.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent ete){
		if (ete.getTarget() instanceof Player){
			Player pl = (Player) ete.getTarget();
			Party plParty = master.getParty(pl.getName());
			if (plParty!=null){
				for (String corpseNames : plParty.getCorps()){
					if (corpseNames.equalsIgnoreCase(pl.getName())){
						ete.setCancelled(true);
						ete.setTarget(null);
					}
				}
			}
		}
	}
	
}
