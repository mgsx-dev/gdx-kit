package net.mgsx.game.plugins.particle2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.EditorAssetManager.ReloadListener;
import net.mgsx.game.plugins.particle2d.Particle2DPlugin;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class Particle2DReloader implements ReloadListener<ParticleEffect> {
	final private EditorScreen editor;
	final private Particle2DPlugin plugin;
	public Particle2DReloader(EditorScreen editor, Particle2DPlugin plugin) {
		this.editor = editor;
		this.plugin = plugin;
	}
	
	@Override
	public void reload(ParticleEffect asset) 
	{
		String reference = editor.assets.getAssetFileName(asset);
		ParticleEffectPool pool = plugin.pools.get(reference);
		
		Array<Particle2DComponent> components = new Array<Particle2DComponent>();
		
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(Particle2DComponent.class).get())){
			Particle2DComponent particle = Particle2DComponent.components.get(entity);
			if(particle.reference.equals(reference) && particle.effect != null){
				particle.effect.free();
				components.add(particle);
			}
		}
		
		int initial = pool.getFree();
		
		pool.clear();
		
		pool = new ParticleEffectPool(asset, initial, pool.max){ // TODO tweak ? how ?
			@Override
			protected PooledEffect newObject() {
				PooledEffect effect = super.newObject();
				effect.scaleEffect(0.01f); // XXX scale once to not have drawbacks !
				return effect;
			}
		};
		plugin.pools.put(reference, pool);
		for(Particle2DComponent component : components){
			component.effect = pool.obtain();
		}
	}

}
