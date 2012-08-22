package de.minefagroup.squads.customLists;

import java.util.ArrayList;

public class Party {
	
	private String name;
	private String creator;
	private ArrayList<String> members;
	//          id, count
//	private HashMap<Integer, Integer> loot;
	
	public Party(String creator, String party){
		this.name=party;
		this.creator=creator;
		members = new ArrayList<String>();
//		loot = new HashMap<Integer, Integer>();
		addMember(creator);
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
	
	public void rmCreator(){
		creator = "";
	}
	
	public Boolean isCreator(String clName){
		return (clName.equalsIgnoreCase(creator));
	}
	
	public ArrayList<String> getMember(){
		return members;
	}
	
	public void addMember(String plName){
		members.add(plName.toLowerCase());
	}
	
	public void rmMember(String plName){
		members.remove(plName.toLowerCase());
	}
	
	public String getName(){
		return name;
	}
	
	public int size(){
		return members.size();
	}
	
	
}
