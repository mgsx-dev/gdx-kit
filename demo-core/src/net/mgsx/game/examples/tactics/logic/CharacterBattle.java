package net.mgsx.game.examples.tactics.logic;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.tactics.model.CharacterDef;

public class CharacterBattle 
{
	public CharacterBattle(CharacterDef c) {
		def = c;
		life = c.life;
		turns = 0;
	}

	public CharacterDef def;
	
	/** computed from intrinsic values and effects */
	public int life;
	public int protection;
	
	public float turns;
	
	public transient Array<CardBattle> cards = new Array<CardBattle>();
	
	public Array<EffectBattle> effects = new Array<EffectBattle>();
}
