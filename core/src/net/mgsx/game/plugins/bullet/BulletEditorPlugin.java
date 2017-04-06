package net.mgsx.game.plugins.bullet;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.bullet.tools.BulletMeshTool;
import net.mgsx.game.plugins.bullet.tools.BulletSphereTool;

@PluginDef(dependencies=BulletPlugin.class, requires="com.badlogic.gdx.physics.bullet.Bullet")
public class BulletEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new BulletMeshTool(editor));
		editor.addTool(new BulletSphereTool(editor));
		editor.entityEngine.addSystem(new BulletWorldDebugSystem(editor));
	}

}
