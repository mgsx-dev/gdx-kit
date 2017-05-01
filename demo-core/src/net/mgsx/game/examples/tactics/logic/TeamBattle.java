package net.mgsx.game.examples.tactics.logic;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.tactics.model.CharacterDef;
import net.mgsx.game.examples.tactics.model.TeamDef;

public class TeamBattle {
	public Array<CharacterBattle> characters = new Array<CharacterBattle>();
	
	public TeamBattle(TeamDef def) {
		for(CharacterDef c : def.characters){
			characters.add(new CharacterBattle(c));
		}
	}
}
