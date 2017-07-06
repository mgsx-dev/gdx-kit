package net.mgsx.game.plugins.bullet;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.bullet.tools.BulletBoundaryTool;
import net.mgsx.game.plugins.bullet.tools.BulletEmitterTool;
import net.mgsx.game.plugins.bullet.tools.BulletMeshTool;
import net.mgsx.game.plugins.bullet.tools.BulletShooterTool;
import net.mgsx.game.plugins.bullet.tools.BulletSphereTool;
import net.mgsx.game.plugins.bullet.tools.BulletThirdPersonTool;

@PluginDef(dependencies=BulletPlugin.class, requires="com.badlogic.gdx.physics.bullet.Bullet")
public class BulletEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new BulletMeshTool(editor));
		editor.addTool(new BulletSphereTool(editor));
		editor.addTool(new BulletBoundaryTool(editor));
		editor.addTool(new BulletThirdPersonTool(editor));
		editor.addTool(new BulletShooterTool(editor));
		editor.addTool(new BulletEmitterTool(editor));
		editor.entityEngine.addSystem(new BulletWorldDebugSystem(editor));
	}

}
