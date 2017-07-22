package net.mgsx.game.services.gapi;

public class Achievement 
{
	// definition part
	public String id, name, description;
	public String unlockedIconUrl;
	public String revealedIconUrl;
	public int totalSteps;
	public String type;
	
	// player part
	public int currentSteps;
	public String state;

	public boolean isUnlocked() {
		return "UNLOCKED".equals(state);
	}

	public boolean isHidden() {
		return "HIDDEN".equals(state);
	}
	
	public boolean isRevealed() {
		return "REVEALED".equals(state);
	}
	
	public boolean isStandard() {
		return "STANDARD".equals(type);
	}
	
	public boolean isIncremental() {
		return "INCREMENTAL".equals(type);
	}

	public String getIconUrl(){
		return isUnlocked() ? unlockedIconUrl : revealedIconUrl;
	}
}
