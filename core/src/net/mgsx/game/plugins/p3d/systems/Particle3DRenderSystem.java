package net.mgsx.game.plugins.p3d.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Inject;

public class Particle3DRenderSystem extends EntitySystem{

	@Inject Particle3DSystem system;
	
	private ModelBatch modelBatch;

	private GameScreen screen;
	
	public Particle3DRenderSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TRANSPARENT);
		modelBatch = new ModelBatch();
		this.screen = screen;
	}
	
	@Override
	public void update(float deltaTime) {
		
		system.pointSpriteBatch.setCamera(screen.camera);
		system.billboardBatch.setCamera(screen.camera);
		
		system.particleSystem.begin();
		system.particleSystem.draw();
		system.particleSystem.end();
		
		modelBatch.begin(screen.camera);
		modelBatch.render(system.particleSystem);
		modelBatch.end();
	}
	
}
