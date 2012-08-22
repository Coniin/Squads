package de.minefagroup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.minefagroup.squads.Party;
import de.minefagroup.squads.Squads;

public class PartyCmdExecutor implements CommandExecutor {
	
	Squads master;
	
	public PartyCmdExecutor(Squads master){
		this.master = master;
	}
	
	@Override
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
			String partyN = master.getMetadataString(pl, "Party", master);
			if (partyN!=null && !partyN.isEmpty()){
				pl.sendMessage("U r Member of the Group \"" +partyN+"\"");
			} else {
				pl.sendMessage("U r ronry rike a ronry worf!");
			}
		}
	//--------
		public void createParty(Player pl, String partyN){
			if (!master.partyExists(partyN)){
				String creatorName = pl.getName();
				Party newParty = new Party(creatorName, partyN);
				master.registerParty(newParty);
				master.setMetadata(pl, "Party",
						(Object) newParty.getName(), master);
				pl.sendMessage("U created a new Party called: "+ partyN);
			} else {
				pl.sendMessage("There is already a Party called "+ partyN);
			}	
		}
		
		public void joinParty(Player pl, String partyN){
			Party plParty = master.getParty(partyN);
			String oldParty = master.getMetadataString(pl, "Party", master);
			if (oldParty!=null && !oldParty.isEmpty()){
				pl.sendMessage("U have to leave ur Group "+oldParty+" first!");
			} else {
			if (plParty != null) {
				plParty.addMember(pl.getName());
				master.setMetadata(pl, "Party",
						(Object) plParty.getName(), master);
				pl.sendMessage("U joined the Party " + partyN);
				for (String member: plParty.getMember()){
					master.getPlayer(member).sendMessage(pl.getName()+" has joined ur Party!");
				}
			} else {
				pl.sendMessage("There is no Party, known by this name!");
			}}
		}
		
		public void leaveParty(Player pl){
			Party plParty = master.getPlayersParty(pl);
			if (plParty!=null){
				plParty.rmMember(pl.getName());
				master.setMetadata(pl, "Party", null, master);
				pl.sendMessage("U leaved Party "+ plParty.getName());
				if (plParty.getMember().size() <= 0) {
					master.unregisterParty(plParty);
				} else {
					for (String member: plParty.getMember()){
						master.getPlayer(member).sendMessage(pl.getName()+" has left ur Party!");
					}
				}
			} else {
				pl.sendMessage("U cant leave Nothing!");
			}

		}
		
		public void listPartys(CommandSender cmdSd){
			cmdSd.sendMessage("Partys:");
			int i = 1;
			for (Party parties : master.getOpenPartys()){
				cmdSd.sendMessage(i++ +". "+parties.getName());
			}
		}
		
		public void kickMember(Player pl, String toKick){
			Party plParty = master.getPlayersParty(pl);
			if (plParty!=null){
				if (plParty.isCreator(pl.getName())){
					if (plParty.isMember(toKick)){
						plParty.rmMember(toKick);
						master.getPlayer(toKick).sendMessage("U got kicked from Party "+plParty.getName());
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
			Party plParty = master.getPlayersParty(pl);
			if (plParty!=null){
				String creator = plParty.getCreator();
				pl.sendMessage("#-----"+plParty.getName()+"-----#");
				pl.sendMessage("Creator: "+master.colorByHealth(master.getPlayer(creator))+creator);
				pl.sendMessage("Members:");
				for (String member: plParty.getMember()){
					if (!member.equalsIgnoreCase(creator)){
						pl.sendMessage("     "+master.colorByHealth(master.getPlayer(member))+member);
					}
				}
			} else {
				pl.sendMessage("U must be Member of a Party!");
			}
		}
			
	
}
