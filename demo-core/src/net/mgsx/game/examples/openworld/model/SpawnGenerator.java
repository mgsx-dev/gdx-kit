package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class SpawnGenerator {
	
	private Array<FreemindNode> items = new Array<FreemindNode>();
	public SpawnGenerator() {
		for(FreemindNode item : OpenWorldModel.items()){
			FreemindNode spawnDef = item.child("spawnable");
			if(spawnDef.exists()){
				items.add(item);
			}
		}
		
	}

	public void generate(Array<OpenWorldElement> result) 
	{
		if(items.size == 0) return;
		
		FreemindNode item = items.get((int)(MathUtils.random() * items.size));
		FreemindNode spawnDef = item.child("spawnable");
		
		// clustering
		FreemindNode cluster = spawnDef.child("cluster");
		int min = cluster.first().asInt(1);
		int max = cluster.child(1).asInt(min);
		int count = MathUtils.random(min, max);
		for(int i=0 ; i<count ; i++){
			OpenWorldElement element = OpenWorldModel.generateNewElement(item);
			result.add(element);
		}
	}
}
