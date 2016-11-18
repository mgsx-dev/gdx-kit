package net.mgsx.game.plugins.particle2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("p2d")
@EditableComponent(autoTool=false)
public class Particle2DComponent implements Component, Duplicable
{
	
	public static ComponentMapper<Particle2DComponent> components = ComponentMapper.getFor(Particle2DComponent.class);
	public String reference;
	public PooledEffect effect;
	final public BoundingBox localBox = new BoundingBox();
	final public Vector2 position = new Vector2();
	public boolean paused;
	
	@Override
	public Component duplicate(Engine engine) {
		Particle2DComponent clone = engine.createComponent(Particle2DComponent.class);
		clone.reference = reference;
		return clone;
	}
	
	@Editable
	public void stop(){
		paused = true;
		effect.allowCompletion();
		
	}
	@Editable
	public void start(){
		paused = false;
		effect.start();
		
	}

}
