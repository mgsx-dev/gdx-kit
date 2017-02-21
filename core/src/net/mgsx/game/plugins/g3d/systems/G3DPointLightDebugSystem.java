package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;

@EditableSystem(isDebug=true)
public class G3DPointLightDebugSystem extends IteratingSystem
{
	@Inject protected DebugRenderSystem editor;
	
	public G3DPointLightDebugSystem() {
		super(Family.all(PointLightComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	public void update(float deltaTime) {
		editor.shapeRenderer.begin(ShapeType.Line);
		super.update(deltaTime);
		editor.shapeRenderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		editor.shapeRenderer.setColor(Hidden.components.has(entity) ? Color.BLACK : Color.WHITE);
		PointLightComponent light = PointLightComponent.components.get(entity);
		editor.shapeRenderer.circle(light.light.position.x, light.light.position.y, light.light.intensity);
		
	}
}
