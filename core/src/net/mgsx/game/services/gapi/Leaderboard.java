package net.mgsx.game.services.gapi;

public class Leaderboard {
	
	// definitions
	public String id, name;
	public String iconUrl;
	public String order;
	
	// TODO player score ... ?
	
	public boolean isAscending(){
		return "LARGER_IS_BETTER".equals(order);
	}
	public boolean isDescending(){
		return "LARGER_IS_BETTER".equals(order);
	}
}
