package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

public class PixelEffectBindSystem extends EntitySystem
{
	private PlatformerPostProcessing effectSystem;
	private G3DRendererSystem renderSystem; 
	
	public PixelEffectBindSystem(PlatformerPostProcessing effectSystem) 
	{
		super(GamePipeline.BEFORE_RENDER);
		this.effectSystem = effectSystem;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.renderSystem = engine.getSystem(G3DRendererSystem.class);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(!effectSystem.settings.enabled) return;
		effectSystem.validate();
		
		effectSystem.getMainTarget().begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		renderSystem.fboStack.add(effectSystem.getMainTarget());
	}
}
