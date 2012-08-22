package de.minefagroup.squads;

import java.util.ArrayList;

public class Party {
	
	private String name;
	private String creator;
	private ArrayList<String> members;
	//      plName, aktivated
	private ArrayList<String> deadPlayers;
	//          id, count
//	private HashMap<Integer, Integer> loot;
	
	public Party(String creatorN, String partyN){
		this.name=partyN;
		this.creator=creatorN;
		members = new ArrayList<String>();
		deadPlayers = new ArrayList<String>();
//		loot = new HashMap<Integer, Integer>();
		addMember(creatorN);
	}
		
	public Boolean isMember(String plName){
		for (String names: members){
			if (names.equalsIgnoreCase(plName)){
				return true;
			}
		}
		return false;
	}
	
	public String getCreator(){
		return creator;
	}
	
	public Boolean isCreator(String clName){
		return (clName.equalsIgnoreCase(creator));
	}
	
	public ArrayList<String> getMember(){
		return members;
	}
	
	public void addMember(String plName){
		members.add(plName);
	}
	
	public void rmMember(String plName){
		members.remove(plName);
		//Auszahlen
	}
	
	public void memberSetDead(String plName){
		if (!deadPlayers.contains(plName)){
			deadPlayers.add(plName);
		}
	}
	
	public void memberSetLive(String plName){
		if (deadPlayers.contains(plName)){
			deadPlayers.remove(plName);
		}
	}
	
	public ArrayList<String> getCorps(){
		return deadPlayers;
	} 
	
	public String getName(){
		return name;
	}
}
