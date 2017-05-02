package net.mgsx.game.examples.tactics.model;

import com.badlogic.gdx.utils.Array;

public class CharacterDef {

	public String id;
	public int life;
	public int protection;
	public String faction;
	
	public transient Model model;
	
	public Array<String> cards = new Array<String>();
}
