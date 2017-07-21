package net.mgsx.game.plugins.p3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("p3d")
@EditableComponent(autoTool=false, autoClone=true)
public class Particle3DComponent implements Component, Poolable
{
	
	public final static ComponentMapper<Particle3DComponent> components = ComponentMapper
			.getFor(Particle3DComponent.class);
	
	@Editable
	public boolean autoRemove = false;

	public ParticleEffect effectModel;
	public transient ParticleEffect effect;
	public transient boolean paused;

	public transient ParticleSystem particleSystem;

	@Editable
	public void stop(){
		// TODO not working, see https://github.com/libgdx/libgdx/wiki/3D-Particle-Effects#stop-new-particle-emission-but-let-existing-particles-finish-playing
		paused = true;
		if(effect != null)
			effect.end();
	}
	@Editable
	public void start(){
		paused = false;
		if(effect != null)
			effect.start();
		
	}
	
	@Override
	public void reset() {
		if(effect != null){
			particleSystem.remove(effect);
			effect = null; // TODO pool ?
		}
		effectModel = null;
	}
}
