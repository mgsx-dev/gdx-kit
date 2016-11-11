package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2dold.Box2DPresets.Box2DPreset;

public class PresetTool extends Tool
{
	private WorldItem worldItem;
	private Box2DPreset preset;
	
	public PresetTool(String name, Editor editor, WorldItem worldItem, Box2DPreset preset) {
		super(name, editor);
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
