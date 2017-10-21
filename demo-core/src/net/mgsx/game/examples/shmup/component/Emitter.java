package net.mgsx.game.examples.shmup.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Emitter implements Component, Poolable
{
	public final static ComponentMapper<Emitter> components = ComponentMapper.getFor(Emitter.class);
	
	public transient int remains;
	public transient float timeout;

	@Override
	public void reset() {
		remains = 0;
		timeout = 0;
	}
}
