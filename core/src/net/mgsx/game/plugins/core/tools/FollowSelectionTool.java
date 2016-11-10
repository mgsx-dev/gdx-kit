package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.tools.Tool;

public class FollowSelectionTool extends Tool
{
	private boolean track = false;
	private Vector3 target = new Vector3();
	public FollowSelectionTool(Editor editor) {
		super(editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.F){
			track = !track;
			return true;
		}
		return super.keyDown(keycode);
	}
	
	@Override
	public void render(Batch batch) 
	{
		if(track)
		{
			Entity selected = editor.getSelected();
			if(selected != null){
				Movable movable = selected.getComponent(Movable.class);
				if(movable != null){
					movable.getPosition(selected, target);
					target.lerp(editor.camera.position, 0.97f); // track smooth
					editor.camera.position.x = target.x;
					editor.camera.position.y = target.y; // XXX TODO + 0.05f shift should be configurable
				}
			}
			
		}
	}
}
