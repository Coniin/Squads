package de.minefagroup.squads.Listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.minefagroup.squads.Squads;
import de.minefagroup.squads.customLists.Party;

public class DamageListener implements Listener{
	
	Squads master;
	
	public DamageListener(Squads plugin){
		master = plugin;
		master.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent edepass) {
		EntityDamageByEntityEvent edbee = edepass;
		damageDoneByPlayer(edbee);
		dyingPlayer(edbee);
	}
	
	public void damageDoneByPlayer(EntityDamageByEntityEvent edepass){
		EntityDamageByEntityEvent edbee = edepass;	
		if (edbee.getEntity() instanceof Player){
			//checkForArrowDamager
			Entity damager = edbee.getDamager();
			if (damager instanceof Arrow){
				damager = ((Arrow) damager).getShooter();
			}
			//checkForDamager
			if (damager instanceof Player){
				Player damagerPl = (Player) damager;			
				Player victim = (Player) edbee.getEntity();
				Party damagerParty = master.getPartyManager().getPlayersParty(damagerPl);
				//checkForDamagerParty
				if (damagerParty!=null){
					if (damagerParty.isMember(victim.getName())){
						edbee.setCancelled(true);
						damagerPl.sendMessage("U cannot hurt some1 in ur Squad!");
					} 			
				} 
			}
		}
	}
	
	//TODO: Check for Explosiondamage, Falldamage etc
	public void dyingPlayer(EntityDamageByEntityEvent edepass){
		EntityDamageByEntityEvent edbee = edepass;
		if (edbee.getEntity() instanceof Player){
			Player pl = (Player) edbee.getEntity();			
			//check if Player is in Party, else stop
			if (!master.getPartyManager().isInParty(pl)){
				return;
			}
			if (edbee.getDamage()>=pl.getHealth()){
				//SetReviveSign
				Location loc = pl.getLocation();
				World w = loc.getWorld();
				Block b = w.getBlockAt(loc);			
				if (b.isEmpty()){
					b.setType(Material.SIGN_POST);
					Sign s = (Sign) b.getState();
					s.setLine(0, pl.getName());
					s.setLine(1, "To revieve");
					s.setLine(2, "destroy sign!");
					s.update();
					//TODO: saveBlockforRevive
				} else {
					return;
				}
				edbee.setCancelled(true);				
				//HidePlayer
				for (Player toHideFrom : pl.getServer().getOnlinePlayers()){
					toHideFrom.hidePlayer(pl);
				}			
				//Stop targetingPlayer
				Entity damager = edbee.getDamager();
				if (damager instanceof Arrow){
					damager = ((Arrow) damager).getShooter();
				}
				if (damager instanceof Creature){
					((Creature) damager).setTarget(null);
				}
				//TODO: StopPlayerMovement
				master.getGraveyard().playerDied(pl.getName(), loc);
			}
		}
	}
	
}
