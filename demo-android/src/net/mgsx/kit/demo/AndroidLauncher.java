package net.mgsx.kit.demo;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;
import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.helpers.NativeService.NativeServiceInterface;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.meta.StaticClassRegistry;
import net.mgsx.game.examples.openworld.OpenWorldEditorPlugin;
import net.mgsx.kit.KitClass;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		
		ClassRegistry.instance = new StaticClassRegistry(KitClass.class);
		
		// TODO refactor has default none service, just log some warning ?
		NativeService.instance = new NativeServiceInterface() {
			
			@Override
			public void openSaveDialog(DialogCallback callback) {
				callback.cancel();
			}
			
			@Override
			public void openLoadDialog(DialogCallback callback) {
				callback.cancel();
			}
		};
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new OpenWorldEditorPlugin());
		editConfig.path = "openworld/openworld-scene-minimal.json";
		
		
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new EditorApplication(editConfig){
			@Override
			public void create() {
				Pd.audio.create(new PdConfiguration());
				super.create();
			}
		}, config);
	}
}
