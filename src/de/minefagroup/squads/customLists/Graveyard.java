package de.minefagroup.squads.customLists;

import java.util.ArrayList;

public class Graveyard {
	
	ArrayList<String> deadPlayers;
	
	public Graveyard(){
		deadPlayers = new ArrayList<String>();
	}
	
	public ArrayList<String> getCorpses(){
		return deadPlayers;
	}
	
	public boolean isDead(String plName){
		return deadPlayers.contains(plName);
	}
	
	public void playerDied(String plName){
		deadPlayers.add(plName);
	}
	
	public void playerRevieved(String plName){
		deadPlayers.remove(plName);
	}
	
}
