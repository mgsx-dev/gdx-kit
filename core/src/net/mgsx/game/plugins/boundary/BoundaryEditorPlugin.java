package net.mgsx.game.plugins.boundary;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.boundary.systems.BoundaryDebugSystem;

public class BoundaryEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final Editor editor) 
	{
		editor.addTool(new RectangleTool("Boundary", editor) {
			
			@Override
			protected void create(Vector2 startPoint, Vector2 endPoint) {
				Entity entity = editor.getSelected();
				BoundaryComponent boundary = BoundaryComponent.components.get(entity);
				if(boundary == null){
					boundary = new BoundaryComponent();
					entity.add(boundary);
				}
				boundary.box.set(new Vector3(startPoint.x, startPoint.y, -1), new Vector3(endPoint.x, endPoint.y, 1));
			}
		});
		
		editor.entityEngine.addSystem(new BoundaryDebugSystem(editor));

	}
}
