package de.minefagroup.squads.customLists;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

public class Graveyard {
	
	ArrayList<String> deadPlayers;
	//		Playername, GravestoneLocation
	HashMap<String, Location> graveStones;
	
	public Graveyard(){
		deadPlayers = new ArrayList<String>();
		graveStones = new HashMap<String, Location>();
	}
	
	public ArrayList<String> getCorpses(){
		return deadPlayers;
	}
	
	public boolean isDead(String plName){
		return deadPlayers.contains(plName);
	}
	
	public void playerDied(String plName, Location plLoc){
		deadPlayers.add(plName);
		graveStones.put(plName, plLoc);
	}
	
	public void playerRevieved(String plName){
		deadPlayers.remove(plName);
		graveStones.remove(plName);
	}
	
	public String whosGrave(Location loc){
		for (String name: graveStones.keySet()){
			if (loc.distance(graveStones.get(name))<=1){
				return name;
			}
		}
		return null;
	}
	
	public boolean isGrave(Location loc){
		return (graveStones.containsValue(loc));
	}
	
}
