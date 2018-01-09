package net.mgsx.game.examples.platformer.rendering;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.graphics.model.FBOModel;

public class PixelEffectBindSystem extends EntitySystem
{
	private PlatformerPostProcessing effectSystem;
	
	@Inject FBOModel fboModel;
	
	public PixelEffectBindSystem(PlatformerPostProcessing effectSystem) 
	{
		super(GamePipeline.BEFORE_RENDER);
		this.effectSystem = effectSystem;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(!effectSystem.settings.enabled) return;
		effectSystem.validate();
		
		fboModel.push(effectSystem.getMainTarget());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
}
