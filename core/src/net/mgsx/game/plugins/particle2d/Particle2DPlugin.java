package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;
import net.mgsx.game.plugins.particle2d.systems.P2DBoundarySystem;
import net.mgsx.game.plugins.particle2d.systems.P2DCullingSystem;
import net.mgsx.game.plugins.particle2d.systems.P2DRenderSystem;
import net.mgsx.game.plugins.particle2d.systems.P2DTransformSystem;
import net.mgsx.game.plugins.particle2d.systems.P2DUpdateSystem;

@PluginDef(components={Particle2DComponent.class})
public class Particle2DPlugin implements Plugin
{
	// shared model accross systems
	final public ObjectMap<String, ParticleEffectPool> pools = new ObjectMap<String, ParticleEffectPool>();
	// final public Array<PooledEffect> effects = new Array<PooledEffect>();
	
	@Override
	public void initialize(final GameScreen engine) 
	{
		// serialize ParticleEffect type as asset reference.
		engine.registry.addSerializer(ParticleEffect.class, new AssetSerializer<ParticleEffect>(ParticleEffect.class));
		
		// just serialize reference (TODO use annotations for this kind of things and use asset serializer above !)
		// it's tricky here because we need reference later for pool (maybe use particle effect directly instead of
		// String reference, pools could be indexed by Particle Effect object)
		engine.registry.addSerializer(Particle2DComponent.class, new AssetSerializer<Particle2DComponent>(Particle2DComponent.class) {
			@Override
			public void write(Json json, Particle2DComponent object, Class knownType) {
				json.writeObjectStart();
				json.writeValue("reference", reference(object.reference));
				json.writeObjectEnd();
			}

			@Override
			public Particle2DComponent read(Json json, JsonValue jsonData, Class type) 
			{
				Particle2DComponent object = new Particle2DComponent();
				json.readField(object, "reference", jsonData);
				return object;
			}
		});
		
		engine.entityEngine.addEntityListener(Family.one(Particle2DComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) 
			{
				// TODO doesn't work !
				Particle2DComponent model = (Particle2DComponent)entity.remove(Particle2DComponent.class);
				if(model != null){
					// remove emitters (live particles!)
					if(model.effect != null)
						model.effect.free();
					//effects.removeValue(model.effect, true);
					model.effect = null;
				}
			}
			
			@Override
			public void entityAdded(Entity entity) 
			{
				Particle2DComponent model = entity.getComponent(Particle2DComponent.class);
				if(model != null){
					// create a pool for it if not existing
					ParticleEffectPool pool = pools.get(model.reference);
					if(pool == null){
						ParticleEffect template = engine.assets.get(model.reference, ParticleEffect.class);
						pool = new ParticleEffectPool(template, 100, 10000){ // TODO tweak ? how ?
							@Override
							protected PooledEffect newObject() {
								PooledEffect effect = super.newObject();
								effect.scaleEffect(0.01f); // XXX scale once to not have drawbacks !
								return effect;
							}
						}; 
						pools.put(model.reference, pool);
					}
					PooledEffect effect = pool.obtain();
					model.effect = effect;
					model.effect.setPosition(model.position.x, model.position.y);
					// effects.add(effect);
				}
			}
		});
		
		// systems
		engine.entityEngine.addSystem(new P2DTransformSystem());
		engine.entityEngine.addSystem(new P2DBoundarySystem());
		engine.entityEngine.addSystem(new P2DCullingSystem());
		engine.entityEngine.addSystem(new P2DUpdateSystem());
		engine.entityEngine.addSystem(new P2DRenderSystem(engine));
	}
	
}
