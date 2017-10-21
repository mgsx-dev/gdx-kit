package net.mgsx.game.examples.convoy.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.examples.convoy.model.Goods;
import net.mgsx.game.examples.convoy.model.MaterialType;

@EditableComponent
public class Conveyor implements Component
{
	
	public final static ComponentMapper<Conveyor> components = ComponentMapper.getFor(Conveyor.class);
	
	public Vector2 position = new Vector2();
	
	public int capacity;
	
	public Array<Goods> goods = new Array<Goods>();

	public float angle;
	
	public float oil, oilMax;
	
	public ObjectMap<MaterialType, Float> materials = new ObjectMap<MaterialType, Float>();
	
	public float oilRequired;
	
	public float storageCapacity;

	public float autoOilRate = .1f;

	public float storageUsed() {
		float total = 0;
		for(Entry<MaterialType, Float> mat : materials){
			total += mat.value;
		}
		return total;
	}
}
