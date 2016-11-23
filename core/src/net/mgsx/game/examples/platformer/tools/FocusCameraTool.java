package net.mgsx.game.examples.platformer.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.platformer.components.FocusComponent;
import net.mgsx.game.examples.platformer.components.PlayerTracker;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;

public class FocusCameraTool extends Tool
{

	public FocusCameraTool(EditorScreen editor) {
		super("Camera Focus", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		editor.setInfo("Make all Culling cameras follow current selection");
		
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(CameraComponent.class, CullingComponent.class).get())){
			entity.add(editor.entityEngine.createComponent(PlayerTracker.class));
		}
		
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(FocusComponent.class).get())){
			entity.remove(FocusComponent.class);
		}
		
		for(Entity entity : editor.selection){
			entity.add(editor.entityEngine.createComponent(FocusComponent.class));
		}
	}
	
}
