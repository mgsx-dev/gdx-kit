package net.mgsx.kit.test;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.launcher.DesktopApplication;

// TODO tutorial instead !
public class KitSystemTest
{
	@PluginDef(category="test")
	public class KitPluginTest extends EditorPlugin {

		private EntitySystem system;
		
		public KitPluginTest(EntitySystem system) {
			super();
			this.system = system;
		}
	
		@Override
		public void initialize(EditorScreen editor) {
			editor.registry.setTag(system, "test");
			editor.entityEngine.addSystem(system);
		}
		
	}
	public KitSystemTest(EntitySystem system) {
		
		ClassRegistry.instance = new ReflectionClassRegistry(
				ReflectionClassRegistry.kitCorePlugin
				);
		

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new KitEditorPlugin());
		editConfig.plugins.add(new AshleyEditorPlugin());
		editConfig.plugins.add(new KitPluginTest(system));
		editConfig.autoSavePath = null;
		
		final EditorApplication editor = new EditorApplication(editConfig);
		
		new DesktopApplication(editor, config);
	}
}
