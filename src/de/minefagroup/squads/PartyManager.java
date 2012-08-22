package de.minefagroup.squads;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minefagroup.squads.customLists.Party;

public class PartyManager {
	
		Squads master;
		ArrayList<Party> openPartys;
		
		public PartyManager(Squads master){
			this.master = master;
			openPartys = new ArrayList<Party>();
		}
	
		//Commands:		
		public void justParty(Player pl){
			String partyN = master.getUtil().getMetadataString(pl, "Party", master);
			if (partyN!=null && !partyN.isEmpty()){
				pl.sendMessage("U r Member of the Group \"" +partyN+"\"");
			} else {
				pl.sendMessage("U r ronry rike a ronry worf! So sad");
			}
		}
	
		public void createParty(Player pl, String partyN){
			if (!partyExists(partyN)){
				String creatorName = pl.getName();
				Party newParty = new Party(creatorName, partyN);
				registerParty(newParty);
				master.getUtil().setMetadata(pl, "Party",
						(Object) newParty.getName(), master);
				pl.sendMessage("U created a new Party called: "+ partyN);
			} else {
				pl.sendMessage("There is already a Party called "+ partyN);
			}	
		}
		
		public void joinParty(Player pl, String partyN){
			Party plParty = getParty(partyN);
			String oldParty = master.getUtil().getMetadataString(pl, "Party", master);
			if (oldParty!=null && !oldParty.isEmpty()){
				pl.sendMessage("U have to leave ur Group "+oldParty+" first!");
			} else {
			if (plParty != null) {
				addMember(pl, partyN);
				pl.sendMessage("U joined the Party " + partyN);
				sayToPartyExcept(pl.getName()+" has joined ur Party!", plParty, pl.getName());
			} else {
				pl.sendMessage("There is no Party, known by this name!");
			}}
		}
		
		public void leaveParty(Player pl){
			Party plParty = getPlayersParty(pl);
			if (plParty!=null){			
				master.getUtil().setMetadata(pl, "Party", null, master);
				pl.sendMessage("U leaved Party "+ plParty.getName());			
				rmMember(pl.getName(), plParty);
				sayToParty(pl.getName()+" has left ur Party!", plParty);
			} else {
				pl.sendMessage("U cant leave Nothing!");
			}

		}
		
		public void listPartys(CommandSender cmdSd){
			cmdSd.sendMessage("Partys:");
			int i = 1;
			for (Party parties : getOpenPartys()){
				cmdSd.sendMessage(i++ +". "+parties.getName());
			}
		}
		
		//kick without checking for creator
		public void kickMember(CommandSender cmdSd, String toKick){
			Player plToKick = master.getUtil().getPlayer(toKick);
			Party plParty = master.getPartyManager().getPlayersParty(plToKick);
			if (plParty.isMember(toKick)){			
				//Say to everyone in Party except the kickSender if he is Member
				Boolean isPlayer = (cmdSd instanceof Player);
				Player pl = (isPlayer ? (Player) cmdSd : null);
				String plName = (isPlayer) ? pl.getName() : "";			
				rmMember(toKick, plParty);
				//inform everyone 'bout kicking
				sayToPartyExcept(toKick + " got kicked from ur Group", plParty, plName);
				plToKick.sendMessage("U got kicked from Party "+plParty.getName());
				cmdSd.sendMessage("U kicked "+toKick+" from Party "+plParty.getName());
				//sayToParty(toKick + " got kicked from ur Group", plParty);				
			} else {
				cmdSd.sendMessage(toKick + " is no Member of "+ plParty.getName());
			}
		}
		
		//kick with check for creator
		public void kickMember(Player pl, String toKick){
			Party plParty = getPlayersParty(pl);
			if (plParty!=null){
				if (plParty.isCreator(pl.getName())){
					kickMember((CommandSender) pl, toKick);
				} else {
					pl.sendMessage("U must be Creator of a Group to kick some1!");
				}
			} else {
				pl.sendMessage("U must be Creator of a Party!");
			} 
		}
		
		public void listMembers(CommandSender cmdSd, Party toList){			
			//check for Player, Op, and Console
			boolean isPlayer = (cmdSd instanceof Player);
		    boolean isOp = isPlayer && ((Player) cmdSd).isOp();
		    boolean isConsole = (cmdSd instanceof ConsoleCommandSender);
		    //define if Player is allowd to see Health
		    boolean showHealth = isOp || isConsole;
		    
			if (toList!=null){
				String creator = toList.getCreator();
				cmdSd.sendMessage("#-----"+toList.getName()+"-----#");
				//Show Health 
				String toSend="Creator: ";
				if (showHealth){
					Player pl = master.getUtil().getPlayer(creator);
					toSend += master.getUtil().colorByHealth(pl);
				}
				toSend+=creator;
				cmdSd.sendMessage(toSend);
				//-
				cmdSd.sendMessage("Members:");
				for (String member : toList.getMember()){
					if (!member.equalsIgnoreCase(creator)){
						toSend="     ";
						if (showHealth){
							Player pl = master.getUtil().getPlayer(member);
							toSend += master.getUtil().colorByHealth(pl);
						}
						toSend += member;
						cmdSd.sendMessage(toSend);
					}
				}
			} else {
				cmdSd.sendMessage("There is no Party by the Name!");
			}
		}
		
		//Util:
		
		public void sayToParty(String toSay, Party pa){
			sayToPartyExcept(toSay, pa, "");
		}
		
		public void sayToPartyExcept(String toSay, Party pa, String playerNotToSay){
			for (String member: pa.getMember()){
				if (!member.equalsIgnoreCase(playerNotToSay)){
					master.getUtil().getPlayer(member).sendMessage(toSay);
				}				
			}
		}
		
		//unregister a Group when the last Member leaves
		public void rmMember(String plName, Party paName){
			paName.rmMember(plName);
			if (paName.isCreator(plName)){
				paName.rmCreator();
			}
			if (paName.size()<=0){
				unregisterParty(paName);
			}
			Player pl = master.getUtil().getPlayer(plName);
			master.getUtil().setMetadata(pl, "Party", null, master);
		}
		
		public void addMember(Player pl, String paName){
			Party plParty = getParty(paName);
			plParty.addMember(pl.getName());
			master.getUtil().setMetadata(pl, "Party",
					(Object) plParty.getName(), master);
		}
		
		public void registerParty(Party toReg) {
			openPartys.add(toReg);
		}

		public void unregisterParty(Party toReg) {
			openPartys.remove(toReg);
		}
	
		public Party getPlayersParty(Player pl) {
			String partyName = master.getUtil().getMetadataString(pl, "Party", master);
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
		

		
}
