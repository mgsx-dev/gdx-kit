package net.mgsx.game.plugins.boundary;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.boundary.systems.BoundaryDebugSystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@PluginDef(dependencies={BoundaryPlugin.class})
public class BoundaryEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		
		editor.addTool(new RectangleTool("Manual Boundary", editor) {
			
			@Override
			protected void create(Vector2 startPoint, Vector2 endPoint) {
				Entity entity = editor.getSelected();
				BoundaryComponent boundary = BoundaryComponent.components.get(entity);
				if(boundary == null){
					boundary = new BoundaryComponent();
					entity.add(boundary);
				}
				Transform2DComponent transform = Transform2DComponent.components.get(entity);
				boundary.box.set(new Vector3(startPoint.x, startPoint.y, -1), new Vector3(endPoint.x, endPoint.y, 1));
				if(transform != null){
					boundary.box.min.sub(transform.position.x, transform.position.y, 0);
					boundary.box.max.sub(transform.position.x, transform.position.y, 0);
				}
			}
		});
		
		editor.entityEngine.addSystem(new BoundaryDebugSystem(editor));

	}
}
