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
import net.mgsx.game.examples.raycast.RayCastRemotePlugin;
import net.mgsx.kit.KitClass;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class SlaveAndroidLauncher extends AndroidApplication {
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
		
//		PdConfiguration.remoteEnabled = true;
//		PdConfiguration.remoteHost = "192.168.0.49";
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new RayCastRemotePlugin());
		editConfig.autoSavePath = null;
		
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
