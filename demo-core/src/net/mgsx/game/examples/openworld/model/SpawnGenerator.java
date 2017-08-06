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

	public OpenWorldElement generate() 
	{
		if(items.size == 0) return null;
		
		FreemindNode item = items.get((int)(MathUtils.random() * items.size));
		return OpenWorldModel.generateNewElement(item);
	}
}
