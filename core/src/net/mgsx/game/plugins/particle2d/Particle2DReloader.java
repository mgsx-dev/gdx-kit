package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.helpers.EntityReloader;

public class Particle2DReloader extends EntityReloader<Particle2DComponent> {

	public Particle2DReloader(Engine engine) {
		super(engine, Family.all(Particle2DComponent.class).get());
	}

	@Override
	protected void reload(Entity entity, Particle2DComponent asset) {
		Particle2DComponent particle = Particle2DComponent.components.get(entity);
		// TODO how to reload ? flush pools and reconfigure them !
	}


}
