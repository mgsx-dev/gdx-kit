package net.mgsx.game.plugins.g2d;

import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.game.plugins.g2d.tools.AddSpriteAnimationTool;
import net.mgsx.game.plugins.g2d.tools.AddSpriteTool;
import net.mgsx.game.plugins.g2d.tools.SpriteModelReloader;
import net.mgsx.game.plugins.g2d.tools.SpriteSelector;

@PluginDef(dependencies={G2DPlugin.class, KitEditorPlugin.class})
public class G2DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		// tools
		editor.addTool(new AddSpriteTool(editor));
		editor.addTool(new AddSpriteAnimationTool(editor));
		editor.addSelector(new SpriteSelector(editor));
		editor.assets.addReloadListener(Texture.class, new SpriteModelReloader(editor.entityEngine));
		

	}
}
