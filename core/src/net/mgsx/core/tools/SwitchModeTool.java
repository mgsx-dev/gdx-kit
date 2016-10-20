package net.mgsx.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.Editor;
import net.mgsx.core.components.Movable;

public class SwitchModeTool extends Tool
{
	public SwitchModeTool(Editor editor) {
		super(editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.TAB){
			editor.toggleMode();
			return true;
		}
		return super.keyDown(keycode);
	}
	
}
