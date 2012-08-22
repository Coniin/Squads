package de.minefagroup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
		
			//check for Player, Op, and Console
			boolean isPlayer = (sender instanceof Player);
		    boolean isOp = isPlayer && ((Player) sender).isOp();
		    boolean isConsole = (sender instanceof ConsoleCommandSender);
		    
	        //If sender isPlayer cast it
	        Player pl = (isPlayer) ? (Player) sender : null;
			
	        //cmd: "/party"
			if (args.length==0){
				//forPlayer
				if (isPlayer){
					justParty(pl);
					return true;
				}
				//forConsole
				if (isConsole){
					listPartys(sender);
					return true;
				}
				return false;
			}
			//cmd: "/party [leave|list|members]"
			if (args.length==1){
				//cmd: "/party leave"
				if (args[0].equalsIgnoreCase("leave")){
					//forPlayer
					if (isPlayer){
						leaveParty(pl);
						return true;
					}
					//forConsole
					if (isConsole){
						sender.sendMessage("A Console in Groups... noooot!");
						return true;
					}
				}
				//cmd: "/party list"
				if (args[0].equalsIgnoreCase("list")){
					//forPlayer&Console
					listPartys(sender);
					return true;
				}
				//cmd: "/party members"
				if (args[0].equalsIgnoreCase("members")){
					//forPlayer
					if (isPlayer){
						listMembers(pl, master.getPlayersParty(pl), true);
					}
				}
				return false;
			}
			//cmd: /party [kick|members|join] (arg1)
			if (args.length == 2){
				//cmd: /party kick membername
				if (args[0].equalsIgnoreCase("kick")){
					String mbName = args[1];
					//ForPlayer
					if (isPlayer){
						if (isOp||(pl.getName().equalsIgnoreCase(master.getPlayersParty(master.getPlayer(mbName)).getCreator()))){
							//TODO
						}
					}
				}
			}
			
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
		
		public void listMembers(Player pl, Party toList, boolean showHealth){
			if (toList!=null){
				String creator = toList.getCreator();
				pl.sendMessage("#-----"+toList.getName()+"-----#");
				//Show Health 
				String toSend="Creator: ";
				if (showHealth){
					toSend+=master.colorByHealth(master.getPlayer(creator));
				}
				toSend+=creator;
				pl.sendMessage(toSend);
				//-
				pl.sendMessage("Members:");
				for (String member: toList.getMember()){
					if (!member.equalsIgnoreCase(creator)){
						toSend="     ";
						if (showHealth){
							toSend+=master.colorByHealth(master.getPlayer(member));
						}
						toSend+=member;
						pl.sendMessage(toSend);
					}
				}
			} else {
				pl.sendMessage("There is no Party by the Name!");
			}
		}
		
		
}
