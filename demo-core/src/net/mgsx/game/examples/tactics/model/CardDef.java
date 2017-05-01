package net.mgsx.game.examples.tactics.model;

public class CardDef {
	public String id;
	public boolean team;
	public boolean self;
	public EffectDef dmg, turns, protection;
	
	public int wait;
	
	/** nb cells (1, 2, 3 ...) in each directions */
	public int radius;
	
	/** angle : 0 : 1 cell, 1 : 3 cells, 2 : 5 cells, 3 : 6 cells */
	public int range;
}
