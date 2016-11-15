package net.mgsx.game.plugins.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.core.tools.NoTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.camera.CameraEditorPlugin;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.systems.PolygonRenderSystem;
import net.mgsx.game.plugins.core.systems.SelectionRenderSystem;
import net.mgsx.game.plugins.core.tools.DeleteTool;
import net.mgsx.game.plugins.core.tools.DuplicateTool;
import net.mgsx.game.plugins.core.tools.FollowSelectionTool;
import net.mgsx.game.plugins.core.tools.OpenTool;
import net.mgsx.game.plugins.core.tools.PanTool;
import net.mgsx.game.plugins.core.tools.ResetTool;
import net.mgsx.game.plugins.core.tools.SaveTool;
import net.mgsx.game.plugins.core.tools.SelectTool;
import net.mgsx.game.plugins.core.tools.SwitchModeTool;
import net.mgsx.game.plugins.core.tools.ZoomTool;

@PluginDef(dependencies={CorePlugin.class, CameraEditorPlugin.class})
public class CoreEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		// systems
		editor.entityEngine.addSystem(new SelectionRenderSystem(editor));
		editor.entityEngine.addSystem(new PolygonRenderSystem(editor));
		
		// order is very important !
		editor.addGlobalTool(new SelectTool(editor));
		editor.addGlobalTool(new ZoomTool(editor));
		editor.addGlobalTool(new PanTool(editor));
		editor.addGlobalTool(new DuplicateTool(editor));
		editor.addGlobalTool(new FollowSelectionTool(editor));
		editor.addGlobalTool(new SwitchModeTool(editor));
		editor.addGlobalTool(new Tool(editor){
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Input.Keys.NUMPAD_0 || keycode == Input.Keys.INSERT){
					editor.getEditorCamera().switchCameras();
					return true;
				}
				return super.keyDown(keycode);
			}
		});
		

		editor.addSuperTool(new NoTool("Select", editor));;
		
		editor.addSuperTool(new OpenTool(editor));;
		editor.addSuperTool(new SaveTool(editor));;
		editor.addSuperTool(new ResetTool(editor));;
		editor.addSuperTool(new DeleteTool(editor));;

		editor.addSuperTool(new ClickTool("Import", editor) {
			private FileHandle file;
			@Override
			protected void activate() {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle selectedFile) {
						file = selectedFile;
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "Patch files (json)";
					}
				});
			}
			@Override
			protected void create(final Vector2 position) 
			{
				for(Entity entity : Storage.load(editor.entityEngine, file, editor.assets, editor.serializers)){
					Movable movable = entity.getComponent(Movable.class);
					if(movable != null){
						movable.move(entity, new Vector3(position.x, position.y, 0)); // sprite plan
					}
				}
				// TODO update things in GUI ?
				
			}
		});;

		editor.addSuperTool(new ClickTool("Proxy", editor) {
			private FileHandle file;
			@Override
			protected void activate() {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle selectedFile) {
						file = selectedFile;
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "Patch files (json)";
					}
				});
			}
			@Override
			protected void create(final Vector2 position) 
			{
				for(Entity entity : Storage.load(editor.entityEngine, file, editor.assets, editor.serializers, true)){
					// TODO add proxy component
					Movable movable = entity.getComponent(Movable.class);
					if(movable != null){
						movable.move(entity, new Vector3(position.x, position.y, 0)); // sprite plan
					}
					ProxyComponent proxy = new ProxyComponent();
					proxy.ref = file.path();
					entity.add(proxy);
				}
				// TODO update things in GUI ?
				
			}
		});

	}

}
