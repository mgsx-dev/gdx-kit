package net.mgsx.game.examples.tactics.logic;

import net.mgsx.game.examples.tactics.model.CardDef;
import net.mgsx.game.examples.tactics.util.Adapter;

public class CardBattle extends Adapter<CardDef> 
{
	public float turns;
	
	public CardBattle(CardDef def) {
		super(def);
	}
}
