package de.minefagroup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minefagroup.squads.PartyManager;
import de.minefagroup.squads.Squads;
import de.minefagroup.squads.customLists.Party;

public class PartyCmdExecutor implements CommandExecutor {
	
	Squads master;
	PartyManager pm;
	
	public PartyCmdExecutor(Squads master){
		this.master = master;
		pm = master.getPartyManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
			//check for Player, Op, and Console
			boolean isPlayer = (sender instanceof Player);
		    boolean isOp = isPlayer && ((Player) sender).isOp();
		    boolean isConsole = (sender instanceof ConsoleCommandSender);
		    
		    //Casting args to Strings
	        String cmdSpec = (args.length > 0) ? args[0].toLowerCase() : "";
	        String cmdArg1 = (args.length > 1) ? args[1].toLowerCase() : "";
		    
	        //If sender isPlayer cast it
	        Player pl = (isPlayer) ? (Player) sender : null;
	        
	        //cmd: "/party"
			if (args.length==0){
				//forPlayer
				if (isPlayer){
					pm.justParty(pl);
					return true;
				}
				//forConsole
				if (isConsole){
					pm.listPartys(sender);
					return true;
				}
				return false;
			}
			
			//cmd: "/party join [partyToJoin]"
			if (cmdSpec.equals("join")){
				//Players, allowed to join Group
				if (isPlayer){
					if (!cmdArg1.isEmpty()){
						pm.joinParty(pl, cmdArg1);
						return true;
					}
					pl.sendMessage("I'm no god, simply a server pls define a group to join: /party join [partToJoin]");
					return true;
				}
				//Console, not allowed to join Group
				if (isConsole){
					sender.sendMessage("Only a Player could join a Party!");
					return true;
				}
				return false;
			}
			
			//cmd: "/party leave"
			if (cmdSpec.equals("leave")){
				//Players, allowed to leave Group
				if (isPlayer){
					pm.leaveParty(pl);
					return true;
				}
				//Console, cant join, so it shouldnt be possible to leave
				if (isConsole){
					sender.sendMessage("U couldnt join a Group. so how to leave one?");
					sender.sendMessage("If u want some1 other to leave use /party kick [playername]");
				}
				return false;
			}
			
			//cmd: "/party create [partyname]"
			if (cmdSpec.equals("create")){
				//Players, allowed to create Party (? Permissions)
				if (isPlayer){
					if (!cmdArg1.isEmpty()){
						pm.createParty(pl, cmdArg1);
						return true;
					}
					pl.sendMessage("U have to define a Partyname, else I will choose one...");
					pl.sendMessage("I think \"Noobgroup\" sounds nice, or \"Failgods\"");
					return true;
				}
				//Console, atm not allowed to create party, prob new cmd: /party create [partyname] [ownername]
				if (isConsole){
					sender.sendMessage("U r here to work, let the Players play in Partys!");
					return true;
				}
				return false;
			}
			
			//cmd "/party list"
			if (cmdSpec.equals("list")){
				//Player&Console, both allowed to see all Groups
				pm.listPartys(sender);
				return true;
			}
			
			//cmd "/party members (groupname)", if no groupname playergroup is choosen
			if (cmdSpec.equals("members")){
				//Players, allowed to simply /party members
				if (isPlayer){
					Party toList = (cmdArg1.isEmpty()) ? pm.getPlayersParty(pl) : pm.getParty(cmdArg1);
					pm.listMembers(pl, toList);
					return true;
				}
				//Console, must define (groupname)
				if (isConsole){
					if (!cmdArg1.isEmpty()){
						pm.listMembers(sender, pm.getParty(cmdArg1));
						return true;
					}
					sender.sendMessage("I'm reading in ur mind to see wich group u want to list");
					sender.sendMessage("Yeah i just need a second, pls wait!");
					return true;
				}
				return false;
			}
			
			//cmd: "/party kick [playerToKick]"
			if (cmdSpec.equals("kick")){
				//Players, only allowed to kick som1 if creator of group or Op
				if (isPlayer){
					if (!cmdArg1.isEmpty()){
						//Op allowd to bypass CreatorPermission
						if (isOp){
							pm.kickMember((CommandSender) pl, cmdArg1);
							return true;
						}
						pm.kickMember(pl, cmdArg1);
						return true;
					}
					pl.sendMessage("Pls define a player to kick: /player kick [playerToKick]");
					return true;
				}
				//Console, allowed to kick som1
				if (isConsole){
					if (!cmdArg1.isEmpty()){
						pm.kickMember(sender, cmdArg1);
						return true;
					}
					sender.sendMessage("Who should be kicked? U? Yeah Prob U should");
					sender.sendMessage("U got kicked by Server! Pls dont reconnect!");
					return true;
				}
			}
			
			//cmd: "/party debug"
			if (cmdSpec.equals("debug")){
				sender.sendMessage(pm.isInParty(pl)+"");
				return true;
			}
			return false;
	}

				

		
}
