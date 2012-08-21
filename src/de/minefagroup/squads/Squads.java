package de.minefagroup.squads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import de.minefagroup.squads.Listener.DamageListener;
import de.minefagroup.squads.Listener.LogoutListener;
import de.minefagroup.squads.Listener.TargetListener;

public class Squads extends JavaPlugin {

	ArrayList<Party> openPartys;

	public void onEnable() {
		openPartys = new ArrayList<Party>();
		new DamageListener(this);
		new LogoutListener(this);
		new TargetListener(this);
	}

	public void onDisable() {
		openPartys = null; // Safe
		for (Player pl: this.getServer().getOnlinePlayers()){
			leaveParty(pl);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("party")) { 
//PlayerAndConsCmds
			//-----------------------				
			if ((args.length>=1)) {
				// cmd /party list
				if (args[0].equalsIgnoreCase("list")){
					listPartys(sender);
					return true;
				}
			}
//PlayerOnlyCmds
			if (sender instanceof Player){
				Player pl = (Player) sender;
				//cmd /party
				if (args.length <= 0) {
					justParty(pl);
					return true;
				}
				//cmd with 1 or more options
				if (args.length >= 1) {
	//-----------------------				
					//cmd /party create
					if (args[0].equalsIgnoreCase("create")) {
						if (args[1]!=null){
							createParty(pl, args[1]);
						} else {
							pl.sendMessage("U must define a Partyname!");
						}
						return true;
					}
	//-----------------------				
					if (args[0].equalsIgnoreCase("join")) {
						if (args[1]!=null){
							joinParty(pl, args[1]);
						} else {
							pl.sendMessage("U must define a Partyname!");
						}
						return true;
					}
	//-----------------------					
					if (args[0].equalsIgnoreCase("leave")) {			
						leaveParty(pl);
						return true;
					}
	//-----------------------				
					if (args[0].equalsIgnoreCase("kick")) {
						if (args[1]!=null){
							kickMember(pl, args[1]);
						} else {
							pl.sendMessage("U must define a Membername!");
						}
						return true;
					}
	//-----------------------				
					if (args[0].equalsIgnoreCase("members")) {
						listMembers(pl);
						return true;
					}
				}

			}
		}
		return false;
	}


	
	
//--------Commands:
//--------
	public void justParty(Player pl){
		String partyN = this.getMetadataString(pl, "Party", this);
		if (partyN!=null && !partyN.isEmpty()){
			pl.sendMessage("U r Member of the Group \"" +partyN+"\"");
		} else {
			pl.sendMessage("U r ronry rike a ronry worf!");
		}
	}
//--------
	public void createParty(Player pl, String partyN){
		if (!partyExists(partyN)){
			String creatorName = pl.getName();
			Party newParty = new Party(creatorName, partyN);
			registerParty(newParty);
			setMetadata(pl, "Party",
					(Object) newParty.getName(), this);
			pl.sendMessage("U created a new Party called: "+ partyN);
		} else {
			pl.sendMessage("There is already a Party called "+ partyN);
		}	
	}
	
	public void joinParty(Player pl, String partyN){
		Party plParty = getParty(partyN);
		String oldParty = this.getMetadataString(pl, "Party", this);
		if (oldParty!=null && !oldParty.isEmpty()){
			pl.sendMessage("U have to leave ur Group "+oldParty+" first!");
		} else {
		if (plParty != null) {
			plParty.addMember(pl.getName());
			setMetadata(pl, "Party",
					(Object) plParty.getName(), this);
			pl.sendMessage("U joined the Party " + partyN);
			for (String member: plParty.getMember()){
				getPlayer(member).sendMessage(pl.getName()+" has joined ur Party!");
			}
		} else {
			pl.sendMessage("There is no Party, known by this name!");
		}}
	}
	
	public void leaveParty(Player pl){
		Party plParty = getPlayersParty(pl);
		if (plParty!=null){
			plParty.rmMember(pl.getName());
			setMetadata(pl, "Party", null, this);
			pl.sendMessage("U leaved Party "+ plParty.getName());
			if (plParty.getMember().size() <= 0) {
				unregisterParty(plParty);
			} else {
				for (String member: plParty.getMember()){
					getPlayer(member).sendMessage(pl.getName()+" has left ur Party!");
				}
			}
		} else {
			pl.sendMessage("U cant leave Nothing!");
		}

	}
	
	public void listPartys(CommandSender cmdSd){
		cmdSd.sendMessage("Partys:");
		int i = 1;
		for (Party parties : openPartys){
			cmdSd.sendMessage(i++ +". "+parties.getName());
		}
	}
	
	public void kickMember(Player pl, String toKick){
		Party plParty = getPlayersParty(pl);
		if (plParty!=null){
			if (plParty.isCreator(pl.getName())){
				if (plParty.isMember(toKick)){
					plParty.rmMember(toKick);
					getPlayer(toKick).sendMessage("U got kicked from Party "+plParty.getName());
					pl.sendMessage("U kicked "+toKick+" from Party "+plParty.getName());
				} else {
					pl.sendMessage(toKick + " is no Member of "+ plParty.getName());
				}
			} else {
				pl.sendMessage("U must be Creator of a Group to kick some1!");
			}
		} else {
			pl.sendMessage("U must be Creator of a Party!");
		} 
	}
	
	public void listMembers(Player pl){
		Party plParty = getPlayersParty(pl);
		if (plParty!=null){
			String creator = plParty.getCreator();
			pl.sendMessage("#-----"+plParty.getName()+"-----#");
			pl.sendMessage("Creator: "+colorByHealth(getPlayer(creator))+creator);
			pl.sendMessage("Members:");
			for (String member: plParty.getMember()){
				if (!member.equalsIgnoreCase(creator)){
					pl.sendMessage("     "+colorByHealth(getPlayer(member))+member);
				}
			}
		} else {
			pl.sendMessage("U must be Member of a Party!");
		}
	}
	
//--------------Util:
//-------------
	private ChatColor colorByHealth(Player pl){
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
	
	
	
	

