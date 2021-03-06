package net.mgsx.game.plugins.procedural.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class HeightFieldDebugSystem extends IteratingSystem
{
	@Inject public DebugRenderSystem debug;
	
	@Editable public float normalScale = 1;
	
	public HeightFieldDebugSystem() {
		super(Family.all(HeightFieldComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		if(hfc.normals != null) {
			debug.shapeRenderer.begin(ShapeType.Line);
			for(int y=0 ; y<hfc.height ; y++){
				for(int x=0 ; x<hfc.width ; x++){
					
					int index = y*hfc.width+x;
					
					float xa = hfc.position.x + x;
					float ya = hfc.position.y + hfc.values[index]; 
					float za = hfc.position.z + y;
					
					Vector3 n = hfc.normals[index];
					
					float xb = xa + n.x * normalScale;
					float yb = ya + n.z * normalScale;
					float zb = za + n.y * normalScale;
					
					debug.shapeRenderer.line(xa, ya, za, xb, yb, zb, Color.GREEN, Color.YELLOW);
				}
			}
			debug.shapeRenderer.end();
		} else{
			debug.shapeRenderer.begin(ShapeType.Point);
			for(int y=0 ; y<hfc.height ; y++){
				for(int x=0 ; x<hfc.width ; x++){
					debug.shapeRenderer.point(
							hfc.position.x + x, 
							hfc.position.y + hfc.values[y*hfc.width+x], 
							hfc.position.z + y);
				}
			}
			debug.shapeRenderer.end();
		}
		
	}

}
