package net.mgsx.game.examples.openworld.model;

public interface OpenWorldGameEventManager {

	void addGameEventListener(OpenWorldGameEventListener listener);
	void removeGameEventListener(OpenWorldGameEventListener listener);
	
	void questAck(String qid);
	
	void actionReport(GameAction action, String type);
}
