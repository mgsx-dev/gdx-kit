package net.mgsx.game.examples.tactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.tactics.TacticsGameScreen;
import net.mgsx.game.examples.ui.CustomUIPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class TacticsLauncher {

	@PluginDef(dependencies={CustomUIPlugin.class})
	private static class EmptyPlugin extends EditorPlugin implements DefaultEditorPlugin
	{
		@Override
		public void initialize(EditorScreen editor) {
		}
	}
	
	public static void main (String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new GameApplication() {
			
			@Override
			public void create() {
				super.create();
				setScreen(new TacticsGameScreen(new Skin(Gdx.files.internal("skins/game-skin.json"))));
			}
		}, config);
	}
}
