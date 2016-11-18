package net.mgsx.game.plugins.parallax;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.parallax.components.ParallaxModel;
import net.mgsx.game.plugins.parallax.tools.ParallaxTool;

/**
 * TODO change parallax to sprite group / layers ...
 */
@PluginDef(components={ParallaxModel.class})
public class ParallaxPlugin extends EditorPlugin {

	private Vector3 camPos = new Vector3();
	private Vector3 pos = new Vector3();
	
	@Override
	public void initialize(final EditorScreen editor) 
	{
		editor.addTool(new ParallaxTool(editor));
		
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(ParallaxModel.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC) { // TODO before render but before transform apply : verify after logic is before before_render
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				ParallaxModel parallax = ParallaxModel.components.get(entity);
				Transform2DComponent transform = Transform2DComponent.components.get(entity);
				
				camPos.set(editor.getRenderCamera().position);
				pos
				.set(parallax.cameraOrigin)
				.sub(camPos)
				.scl(parallax.rateX-1, parallax.rateY-1, 1) // .scl(0.05f) // TODO due to Tool pixelSize * 0.5f
				.add(parallax.objectOrigin);
				
				transform.position.set(pos.x, pos.y);
			}
		});
		
	}
}
