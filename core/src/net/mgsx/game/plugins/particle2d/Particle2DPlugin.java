package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class Particle2DPlugin implements Plugin
{
	
	protected ObjectMap<String, ParticleEffectPool> pools = new ObjectMap<String, ParticleEffectPool>();
	protected Array<PooledEffect> effects = new Array<PooledEffect>();
	
	@Override
	public void initialize(final GameScreen engine) 
	{
		Storage.register(Particle2DComponent.class, "p2d");
		
		Storage.register(new AssetSerializer<ParticleEffect>(ParticleEffect.class));
		
		// just serialize reference (TODO use annotations for this kind of things)
		engine.addSerializer(Particle2DComponent.class, new Serializer<Particle2DComponent>(){

			@Override
			public void write(Json json, Particle2DComponent object, Class knownType) {
				json.writeObjectStart();
				json.writeField(object, "reference");
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
				Particle2DComponent model = (Particle2DComponent)entity.remove(Particle2DComponent.class);
				if(model != null){
					// remove emitters (live particles!)
					model.effect.free();
					effects.removeValue(model.effect, true);
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
					
					effects.add(effect);
				}
			}
		});
		
		// update all emitters
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(Transform2DComponent.class, Particle2DComponent.class).get(), GamePipeline.AFTER_PHYSICS) {
			@Override
			public void update(float deltaTime) 
			{
				super.update(deltaTime);
				
				for (int i = effects.size - 1; i >= 0; i--) {
				    PooledEffect effect = effects.get(i);
				    effect.update(deltaTime);
				    if (effect.isComplete()) {
				        effect.free();
				        effects.removeIndex(i);
				    }
				}
				for(ParticleEffect effect : effects){
					effect.update(deltaTime);
				}
				
				super.update(deltaTime);
			}

			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
				Particle2DComponent p = entity.getComponent(Particle2DComponent.class);
				p.effect.setPosition(t.position.x, t.position.y);
			}
		});
		engine.entityEngine.addSystem(new EntitySystem(GamePipeline.RENDER_TRANSPARENT) {
			private SpriteBatch batch;
			@Override
			public void addedToEngine(Engine engine) {
				super.addedToEngine(engine);
				batch = new SpriteBatch();
			}
			@Override
			public void removedFromEngine(Engine engine) {
				batch.dispose();
				batch = null;
				super.removedFromEngine(engine);
			}
			@Override
			public void update(float deltaTime) 
			{
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				batch.setProjectionMatrix(engine.getRenderCamera().combined);
				batch.begin();
				for (int i = effects.size - 1; i >= 0; i--) {
				    PooledEffect effect = effects.get(i);
				    effect.draw(batch);
				}
				batch.end();
				Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
			}
		});
	}
	
}
