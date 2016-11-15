package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("p2d")
public class Particle2DComponent implements Component, Duplicable
{
	
	public static ComponentMapper<Particle2DComponent> components = ComponentMapper.getFor(Particle2DComponent.class);
	public String reference;
	public PooledEffect effect;
	
	@Override
	public Component duplicate() {
		Particle2DComponent clone = new Particle2DComponent();
		clone.reference = reference;
		return clone;
	}

}
