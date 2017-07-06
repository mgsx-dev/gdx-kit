package net.mgsx.game.plugins.procedural.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem
public class HeightFieldDebugSystem extends IteratingSystem
{
	@Inject public DebugRenderSystem debug;
	
	public HeightFieldDebugSystem() {
		super(Family.all(HeightFieldComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}

	@Override
	public void update(float deltaTime) {
		debug.shapeRenderer.setColor(Color.GREEN);
		debug.shapeRenderer.begin(ShapeType.Point);
		super.update(deltaTime);
		debug.shapeRenderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		for(int y=0 ; y<hfc.height ; y++){
			for(int x=0 ; x<hfc.width ; x++){
				debug.shapeRenderer.point(
						hfc.position.x + x - (hfc.width-1)/2f, 
						hfc.position.y + hfc.values[y*hfc.width+x], 
						hfc.position.z + y - (hfc.height-1)/2f);
			}
		}
		
	}

}
