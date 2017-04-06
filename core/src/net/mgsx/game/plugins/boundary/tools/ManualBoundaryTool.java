package net.mgsx.game.plugins.boundary.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

public class ManualBoundaryTool extends RectangleTool {
	
	@Inject SelectionSystem selection;
	
	public ManualBoundaryTool(EditorScreen editor) {
		super("Manual Boundary", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) {
		Entity entity = selection.selected();
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
}