package net.mgsx.game.examples.openworld.model;

public interface OpenWorldGameEventListener {

	void onQuestUnlocked(String qid);
	void onQuestRevealed(String qid);
	void onSecretUnlocked(String itemId);
	void onPlayerAction(GameAction action, String type);
}
