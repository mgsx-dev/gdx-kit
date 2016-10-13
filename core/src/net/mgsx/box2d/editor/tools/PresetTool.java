package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.box2d.editor.Box2DPresets.Box2DPreset;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.Tool;

public class PresetTool extends Tool
{
	private WorldItem worldItem;
	private Box2DPreset preset;
	
	public PresetTool(String name, Camera camera, WorldItem worldItem, Box2DPreset preset) {
		super(name, camera);
		this.worldItem = worldItem;
		this.preset = preset;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.RIGHT)
		{
			Vector2 pos = unproject(screenX, screenY);
			preset.create(worldItem.items, worldItem.world, pos.x, pos.y);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
}
