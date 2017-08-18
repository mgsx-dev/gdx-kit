package net.mgsx.game.examples.tactics.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class Model {
	public Array<CharacterDef> characters;
	public Array<CardDef> cards;
	public ObjectMap<String, Array<String>> teams;
	public ObjectMap<String, FactionDef> factions;
	
	public static Model load(FileHandle file){
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		Model model = json.fromJson(Model.class, file);
		for(CharacterDef c : model.characters){
			c.model = model;
		}
		return model;
	}

	public CharacterDef getCharacter(String id) {
		for(CharacterDef def : characters)
			if(id.equals(def.id))
				return def;
		return null;
	}
	public CardDef getCards(String id) {
		for(CardDef card : cards)
			if(id.equals(card.id))
				return card;
		return null;
	}

	public TeamDef getTeam(String id) {
		TeamDef def = new TeamDef();
		for(String cid : teams.get(id))
			def.characters.add(getCharacter(cid));
		return def;
	}
}
