package net.mgsx.game.examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.ApplicationWrapper;
import net.mgsx.game.core.helpers.FileHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.pd.PdConfiguration;

public class ExamplesEditorDesktopLauncher {

	private static class MenuScreen extends StageScreen
	{
		public MenuScreen(final ApplicationWrapper wrapper) {
			super(new Skin(Gdx.files.classpath("uiskin.json")));
			
			Table root = new Table(getSkin());
			root.setFillParent(true);
			getStage().addActor(root);
			
			Table table = new Table(getSkin());
			
			root.add(new ScrollPane(table, getSkin()));
			
			// scan example plugins package
			ReflectionClassRegistry scanner = new ReflectionClassRegistry("net.mgsx.game.examples");
			for(final Class plugin : scanner.getSubTypesOf(EditorPlugin.class)){
				
				TextButton bt = new TextButton(plugin.getSimpleName(), getSkin());
				table.add(bt).fillX().row();
				
				bt.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						ClassRegistry.instance = new ReflectionClassRegistry(plugin.getPackage().getName());
						EditorConfiguration editConfig = new EditorConfiguration();
						EditorPlugin pluginInstance = ReflectionHelper.newInstance(plugin);
						editConfig.plugins.add(pluginInstance);
						// boot file from classpath if exists
						FileHandle file = FileHelper.classpath(plugin, "example.json");
						if(file.exists()){
							editConfig.path = file.path();
						}
						editConfig.autoSavePath = null; // disable auto save for examples
						EditorApplication editorApplication = new EditorApplication(editConfig);
						wrapper.setListener(editorApplication);
					}
				});
			}
			
			// XXX Workaround
			Gdx.input.setInputProcessor(getStage());
		}
	}
	
	private ApplicationWrapper wrapper;
	
	public ExamplesEditorDesktopLauncher() 
	{
		// TODO how to configure disabled, remote, enabled ...
		PdConfiguration.disabled = true;
		
		Game menuApplication = new Game() {
			@Override
			public void create() {
				setScreen(new MenuScreen(wrapper));
			}
		};
		wrapper = new ApplicationWrapper(menuApplication);
		new LwjglApplication(wrapper, new LwjglApplicationConfiguration());
	}
	
	public static void main (String[] args) 
	{
		new ExamplesEditorDesktopLauncher();
	}
}
