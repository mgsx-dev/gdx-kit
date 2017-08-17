package net.mgsx.game.examples;

import java.util.Comparator;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.ApplicationWrapper;
import net.mgsx.game.core.helpers.FileHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class ExamplesEditorDesktopLauncher {

	private static class MenuScreen extends StageScreen
	{
		private ApplicationWrapper wrapper;

		public MenuScreen(ApplicationWrapper wrapper) {
			super(new Skin(Gdx.files.classpath("uiskin.json")));
			this.wrapper = wrapper;
			
			Table root = new Table(getSkin());
			root.setFillParent(true);
			getStage().addActor(root);
			
			root.add("Examples");
			root.add("Plugins");
			root.row();
			root.add(new ScrollPane(createLaunchers("net.mgsx.game.examples"), getSkin())).expandY().top();
			root.add(new ScrollPane(createLaunchers("net.mgsx.game.plugins"), getSkin())).expandY().top();
			
			// XXX Workaround since Kit.inputs is not set out of GameApplication context ...
			Gdx.input.setInputProcessor(getStage());
		}
		
		private Table createLaunchers(String forPackage){
			Table table = new Table(getSkin());
			
			// scan example plugins package
			ReflectionClassRegistry scanner = new ReflectionClassRegistry(forPackage);
			Array<Class> plugins = scanner.getSubTypesOf(EditorPlugin.class);
			plugins.sort(new Comparator<Class>() {
				@Override
				public int compare(Class o1, Class o2) {
					return o1.getSimpleName().compareTo(o2.getSimpleName());
				}
			});
			for(final Class plugin : plugins){
				
				TextButton bt = new TextButton(plugin.getSimpleName(), getSkin());
				table.add(bt).fillX().row();
				
				bt.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						// configure classpath scanner for this plugin
						// TODO is it really necessary ? should we force plugin to declare components ?
						// Couldn't this be done automatically for each plugins and dependent plugings ?
						ClassRegistry.instance = new ReflectionClassRegistry(plugin.getPackage().getName());
						EditorConfiguration editConfig = new EditorConfiguration();
						
						// XXX patch for individual plugins
						editConfig.plugins.add(new KitEditorPlugin());
						editConfig.plugins.add(new AshleyEditorPlugin());
						
						// instanciate selected plugin
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
			
			return table;
		}
	}
	
	private ApplicationWrapper wrapper;
	
	public ExamplesEditorDesktopLauncher(final boolean pd) 
	{
		Game menuApplication = new Game() {
			@Override
			public void create() {
				if(pd) Pd.audio.create(new PdConfiguration());
				setScreen(new MenuScreen(wrapper));
			}
		};
		wrapper = new ApplicationWrapper(menuApplication);
		new LwjglApplication(wrapper, new LwjglApplicationConfiguration());
	}
	
	public static void main (String[] args) 
	{
		ObjectSet<String> parameters = new ObjectSet<String>();
		parameters.addAll(args);
		
		// TODO could we abstract a little the pd things with the Pd Plugin ?
		// process arguments. Default configuration is Gdx audio and no Pd at all.
		boolean pd = false;
		if(parameters.contains("-pd")){
			pd = true;
			PdConfiguration.disabled = false;
		} else if(parameters.contains("-pd-remote")){
			PdConfiguration.remoteEnabled = true;
		} else {
			PdConfiguration.disabled = true;
		}
		if(parameters.contains("-no-audio")){
			LwjglApplicationConfiguration.disableAudio = true;
		}
		
		new ExamplesEditorDesktopLauncher(pd);
	}
}
