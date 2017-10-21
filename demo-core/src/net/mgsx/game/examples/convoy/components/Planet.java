package net.mgsx.game.examples.convoy.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.examples.convoy.model.Goods;
import net.mgsx.game.examples.convoy.model.MaterialInfo;
import net.mgsx.game.examples.convoy.model.MaterialType;

@EditableComponent
public class Planet implements Component
{
	
	public final static ComponentMapper<Planet> components = ComponentMapper.getFor(Planet.class);
	
	public Vector2 position = new Vector2();
	
	public float logicSize;
	
	public float radius;
	
	public Array<Goods> goods = new Array<Goods>();

	public String name;
	
	public float gazAvailability;
	
	public ObjectMap<MaterialType, MaterialInfo> materials = new ObjectMap<MaterialType, MaterialInfo>();
}
