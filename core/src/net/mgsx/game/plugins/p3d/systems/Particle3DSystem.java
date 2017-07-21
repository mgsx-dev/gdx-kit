package net.mgsx.game.plugins.p3d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ModelInstanceParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Matrix4;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.plugins.core.components.Transform3DComponent;
import net.mgsx.game.plugins.p3d.components.Particle3DComponent;

public class Particle3DSystem extends IteratingSystem
{
	public ParticleSystem particleSystem;
	public PointSpriteParticleBatch pointSpriteBatch;
	public BillboardParticleBatch billboardBatch;
	public ModelInstanceParticleBatch modelInstanceBatch;
	private GameScreen screen;
	
	public Particle3DSystem(GameScreen screen) {
		super(Family.all(Particle3DComponent.class).get(), GamePipeline.FIRST);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		particleSystem = new ParticleSystem();
		
		pointSpriteBatch = new PointSpriteParticleBatch();
		particleSystem.add(pointSpriteBatch);
		
		billboardBatch = new BillboardParticleBatch();
		particleSystem.add(billboardBatch);
		
		modelInstanceBatch = new ModelInstanceParticleBatch();
		particleSystem.add(modelInstanceBatch);
		
		// needed for fx loading
		screen.registry.putDefaultLoaderParameter(ParticleEffect.class, new ParticleEffectLoadParameter(particleSystem.getBatches()));
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// TODO nothing to do, all is done in component reset method
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Particle3DComponent pfx = Particle3DComponent.components.get(entity);
				pfx.effect = pfx.effectModel.copy(); // TODO pool !
				pfx.effect.init();
				pfx.effect.start();
				pfx.particleSystem = particleSystem;
				particleSystem.add(pfx.effect);
			}
		});
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		particleSystem.update();
	}

	private Matrix4 transform = new Matrix4();
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Particle3DComponent pfx = Particle3DComponent.components.get(entity);
		if(pfx.effect != null && pfx.effect.isComplete()){
			particleSystem.remove(pfx.effect);
			pfx.effect = null; // TODO pool
			if(pfx.autoRemove){
				getEngine().removeEntity(entity);
			}
		}
		// TODO maybe a dedicated system ?
		if(pfx.effect != null){
			Transform3DComponent trans = Transform3DComponent.components.get(entity);
			if(trans != null){
				pfx.effect.setTransform(transform.set(trans.position, trans.rotation));
			}
		}
	}
}
