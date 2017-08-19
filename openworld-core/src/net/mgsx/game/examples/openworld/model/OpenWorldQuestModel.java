package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindMap;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class OpenWorldQuestModel {

	protected FreemindMap map;
	private FreemindNode quest;

	OpenWorldQuestModel(FreemindMap map, String UID) {
		this.map = map;
		this.quest = map.root().child("quests").child(UID);
	}

	public String name() {
		return OpenWorldModel.i18n(quest, "name", OpenWorldModel.missing(quest.asString()));
	}

	public String summary() {
		return OpenWorldModel.i18n(quest, "summary");
	}

	public Array<String> unlocking() {
		Array<String> r = new Array<String>();
		for(FreemindNode q : quest.child("unlock").children()) r.add(q.asString());
		return r;
	}

	public int xp() {
		return quest.child("reward").child("xp").asInt(0);
	}

	// TODO could be multiple ...
	public Array<String> items() {
		Array<String> r = new Array<String>();
		for(FreemindNode q : quest.child("reward").child("items").children()) r.add(q.asString());
		return r;
	}

	public Array<String> knowledges() {
		Array<String> r = new Array<String>();
		for(FreemindNode q : quest.child("reward").child("building").children()) r.add(q.asString());
		return r;
	}

	public static class Requirement {
		public String action, type;
		public int count;
	}
	
	public Array<Requirement> requirements() {
		Array<Requirement> rs = new Array<Requirement>();
		FreemindNode req = quest.child("require");
		
		for(FreemindNode act : req.child("actions").children()){
			for(FreemindNode type : act.children()){
				Requirement r = new Requirement();
				r.action = act.asString();
				r.type = type.asString();
				r.count = type.first().asInt(1);
				rs.add(r);
			}
		}
		
		// TODO other kind of requirements here
		
		// To prevent auto unlocking when model is not complete then add a dummy :
		if(rs.size == 0){
			Requirement r = new Requirement();
			r.type = r.action = "unknown";
			r.count = 1;
			rs.add(r);
		}
		return rs;
	}

}
