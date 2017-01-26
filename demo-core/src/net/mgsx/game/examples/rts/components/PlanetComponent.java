package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("rts.planet")
@EditableComponent
public class PlanetComponent implements Component
{
	// TODO split in "celestal body" with self rotation which influence other planets rotations
	// planet and stars are celest body
	// population have to be refactored to population per race (players / AI / neutral).
	// resources should be split in other components (having resources or not ... ?)
	
	public final static ComponentMapper<PlanetComponent> components = ComponentMapper.getFor(PlanetComponent.class);
	
	@Editable
	public float size = 1;
	@Editable
	public Color color = new Color(Color.WHITE);
	@Editable
	public float health = 1;
	
	public float population;
	public float solarDistance;
	
	public float resources;

	public float maxPopulation;
	
	public int owner = -1;
	
}
